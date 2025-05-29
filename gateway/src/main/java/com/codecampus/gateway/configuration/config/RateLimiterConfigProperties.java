package com.codecampus.gateway.configuration.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterConfigProperties
{
  // Thêm getter cho routes nếu chưa có
  private Map<String, RouteConfig> routes = new HashMap<>();
  // Thêm getter cho defaultConfig nếu chưa có
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
