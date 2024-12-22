package com.example.dataprocessing.service;

import java.sql.SQLException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service
@Slf4j
public class DataService {
 private final JdbcTemplate jdbcTemplate;
 private final RedisTemplate<String, Object> redisTemplate;
 private final ExecutorService executorService;
 private final MeterRegistry meterRegistry;
 private static final Logger log = LoggerFactory.getLogger(DataService.class);

 public DataService(JdbcTemplate jdbcTemplate, RedisTemplate<String, Object> redisTemplate,
                   ExecutorService executorService,
                   MeterRegistry meterRegistry) {
     this.jdbcTemplate = jdbcTemplate;
     this.redisTemplate = redisTemplate;
     this.executorService = executorService;
     this.meterRegistry = meterRegistry;
 }

 @Retryable(retryFor = {SQLException.class}, maxAttempts = 3)
 public <T> CompletableFuture<T> processDataAsync(String key, Supplier<T> databaseOperation) {
     return CompletableFuture.supplyAsync(() -> {
         Timer.Sample timer = Timer.start(meterRegistry);
         
         try {
             // Try cache first
             @SuppressWarnings("unchecked")
             T cachedResult = (T) redisTemplate.opsForValue().get(key);
             if (cachedResult != null) {
                 meterRegistry.counter("cache.hits").increment();
                 return cachedResult;
             }
             
             meterRegistry.counter("cache.misses").increment();
             
             // Execute database operation
             T result = databaseOperation.get();
             
             // Cache the result
             redisTemplate.opsForValue().set(key, result, 1, TimeUnit.HOURS);
             
             timer.stop(Timer.builder("operation.latency")
                 .description("Operation latency")
                 .register(meterRegistry));
             
             return result;
         } catch (Exception e) {
             log.error("Error processing data", e);
             throw new RuntimeException("Failed to process data", e);
         }
     }, executorService);
 }

 public void invalidateCache(String key) {
     redisTemplate.delete(key);
 }
}