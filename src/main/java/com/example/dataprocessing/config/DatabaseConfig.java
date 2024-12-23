package com.example.dataprocessing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DatabaseConfig {
    @Bean
    @ConfigurationProperties("spring.datasource")
    public HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/datasource");
        config.setUsername("root");
        config.setPassword("12345");
        config.setMaximumPoolSize(10);
        return new HikariDataSource(config);
    }
}
