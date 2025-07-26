package com.codecampus.gateway.configuration.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Lớp đọc cấu hình cho Rate Limiter từ file application.properties hoặc application.yml.
 * Các cấu hình gồm defaultConfig và từng route riêng lẻ.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterConfigProperties {
    private Map<String, RouteConfig> routes = new HashMap<>();
    private DefaultConfig defaultConfig = new DefaultConfig();

    @Getter
    @Setter
    public static class RouteConfig {
        private int capacity;
        private int refillTokens;
        private int refillDuration; // seconds
    }

    @Getter
    @Setter
    public static class DefaultConfig {
        private int capacity = 10;
        private int refillTokens = 10;
        private int refillDuration = 60; // seconds
    }

}
