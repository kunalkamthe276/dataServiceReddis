package com.example.dataprocessing.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThreadingConfig {
    @Bean
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(
            5, // Core pool size
            10, // Maximum pool size
            60L, // Keep alive time
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            new ThreadFactoryBuilder().setNameFormat("data-processor-%d").build()
        );
    }
}