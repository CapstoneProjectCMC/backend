package com.codecampus.gateway.configuration.filter;

import com.codecampus.gateway.configuration.config.RateLimiterConfig;
import com.codecampus.gateway.configuration.config.RateLimiterConfigProperties;
import com.codecampus.gateway.dto.api.ApiResponse;
import com.codecampus.gateway.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * GatewayFilterFactory để giới hạn tốc độ request theo từng route và IP.
 * Sử dụng Bucket4j kết hợp cấu hình từ RateLimiterConfigProperties.
 */
@Component
public class RateLimiterGatewayFilterFactory
    extends
    AbstractGatewayFilterFactory<RateLimiterGatewayFilterFactory.Config> {

  private final RateLimiterConfig rateLimiterConfig;
  private final ObjectMapper objectMapper;
  private final RateLimiterConfigProperties configProperties;

  /**
   * Bộ nhớ cache lưu trữ các Bucket cho mỗi cặp (routeId, clientIp).
   */
  private final Map<String, Bucket> bucketCache = new ConcurrentHashMap<>();

  public RateLimiterGatewayFilterFactory(
      RateLimiterConfig rateLimiterConfig,
      ObjectMapper objectMapper,
      RateLimiterConfigProperties configProperties) {
    super(Config.class);
    this.rateLimiterConfig = rateLimiterConfig;
    this.objectMapper = objectMapper;
    this.configProperties = configProperties;
  }

  /**
   * Áp dụng filter giới hạn tốc độ cho một route.
   *
   * @param config cấu hình chứa routeId cần áp dụng rate limit
   * @return GatewayFilter xử lý giới hạn tốc độ
   */
  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      String routeId = config.getRouteId();

      // Lấy IP của người dùng
      String clientIp = "unknown";
      if (exchange.getRequest().getRemoteAddress() != null
          && exchange.getRequest().getRemoteAddress().getAddress() != null) {
        clientIp = exchange.getRequest().getRemoteAddress().getAddress()
            .getHostAddress();
      }

      // Gán RouteId cho IP người dùng -> key
      String key = routeId + ":" + clientIp;

      // Lấy cấu hình từ application.yaml
      RateLimiterConfigProperties.RouteConfig routeConfig =
          configProperties.getRoutes().get(routeId);

      final int capacity;
      final int refillTokens;
      final int refillDuration;

      if (routeConfig != null) {
        capacity = routeConfig.getCapacity();
        refillTokens = routeConfig.getRefillTokens();
        refillDuration = routeConfig.getRefillDuration();
      } else {
        capacity = configProperties.getDefaultConfig().getCapacity();
        refillTokens = configProperties.getDefaultConfig().getRefillTokens();
        refillDuration =
            configProperties.getDefaultConfig().getRefillDuration();
      }

      Bucket bucket = bucketCache.computeIfAbsent(key, k ->
          rateLimiterConfig.createNewBucket(
              capacity,
              refillTokens,
              Duration.ofSeconds(refillDuration)
          )
      );

      if (bucket.tryConsume(1)) {
        return chain.filter(exchange);
      } else {
        return handleRateLimitExceeded(exchange);
      }
    };
  }

  /**
   * Xử lý khi vượt quá giới hạn tốc độ, trả về 429 cùng với JSON thông báo.
   *
   * @param exchange đối tượng ServerWebExchange để ghi response
   * @return Mono hoàn thành khi ghi response
   */
  private Mono<Void> handleRateLimitExceeded(
      ServerWebExchange exchange) {
    exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
    exchange.getResponse().getHeaders()
        .setContentType(MediaType.APPLICATION_JSON);

    ApiResponse<?> apiResponse = ApiResponse.builder()
        .code(ErrorCode.RATE_LIMIT_EXCEEDED.getCode())
        .message(ErrorCode.RATE_LIMIT_EXCEEDED.getMessage())
        .status(ErrorCode.RATE_LIMIT_EXCEEDED.getStatus())
        .build();

    try {
      byte[] bytes = objectMapper.writeValueAsBytes(apiResponse);
      DataBuffer buffer = exchange.getResponse()
          .bufferFactory()
          .wrap(bytes);
      return exchange.getResponse().writeWith(Mono.just(buffer));
    } catch (JsonProcessingException e) {
      return exchange.getResponse().setComplete();
    }
  }

  /**
   * Cấu hình truyền vào cho filter, chỉ chứa routeId.
   */
  @Getter
  @Setter
  public static class Config {
    private String routeId; // ID của route tương ứng
  }
}
