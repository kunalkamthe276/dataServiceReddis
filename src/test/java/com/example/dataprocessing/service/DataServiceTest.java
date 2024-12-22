package com.example.dataprocessing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class DataServiceTest {
    @Autowired
    private DataService dataService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Test
    void testCacheHitAndMiss() {
        String key = "test:key";
        String value = "test-value";
        
        // First call - should be a cache miss
        CompletableFuture<String> future1 = dataService.processDataAsync(key, () -> value);
        assertEquals(value, future1.join());
        
        // Second call - should be a cache hit
        CompletableFuture<String> future2 = dataService.processDataAsync(key, () -> value);
        assertEquals(value, future2.join());
        
        // Verify cache contains value
        assertTrue(redisTemplate.hasKey(key));
    }
}