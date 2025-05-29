package com.codecampus.gateway.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.codecampus.gateway.configuration.config.RateLimiterConfig;
import com.codecampus.gateway.configuration.config.RateLimiterConfigProperties;
import com.codecampus.gateway.configuration.filter.RateLimiterGatewayFilterFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.BlockingBucket;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.BucketListener;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.EstimationProbe;
import io.github.bucket4j.SchedulingBucket;
import io.github.bucket4j.TokensInheritanceStrategy;
import io.github.bucket4j.VerboseBucket;
import java.net.InetSocketAddress;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class RateLimiterGatewayFilterFactoryTest
{
  private RateLimiterGatewayFilterFactory filterFactory;
  private GatewayFilterChain filterChain;

  @BeforeEach
  void setUp() {
    // Mock RateLimiterConfig
    RateLimiterConfig rateLimiterConfig = mock(RateLimiterConfig.class);
    when(rateLimiterConfig.createNewBucket(anyInt(), anyInt(), any(Duration.class)))
        .thenReturn(new TestBucket(2)); //Giới hạn 2 request

    // Mock Config Properties
    RateLimiterConfigProperties configProperties = new RateLimiterConfigProperties();
    RateLimiterConfigProperties.RouteConfig routeConfig = new RateLimiterConfigProperties.RouteConfig();
    routeConfig.setCapacity(2);
    configProperties.getRoutes().put("test_route", routeConfig);

    filterFactory = new RateLimiterGatewayFilterFactory(
      rateLimiterConfig,
      new ObjectMapper(),
      configProperties
    );

    filterChain = mock(GatewayFilterChain.class);
    when(filterChain.filter(any())).thenReturn(Mono.empty());
  }

  @Test
  void whenExceedLimit_thenReturn429(){
    RateLimiterGatewayFilterFactory.Config config = new RateLimiterGatewayFilterFactory.Config();
    config.setRouteId("test_route");
    GatewayFilter filter = filterFactory.apply(config);

    ServerWebExchange exchange1 = createExchange("127.0.0.1");
    ServerWebExchange exchange2 = createExchange("127.0.0.1");
    ServerWebExchange exchange3 = createExchange("127.0.0.1");

    // Request 1: thành công
    filter.filter(exchange1, filterChain).block();

    // Request 2: thành công
    filter.filter(exchange2, filterChain).block();

    // Request 3: vượt giới hạn
    filter.filter(exchange3, filterChain).block();

    // Kiểm tra status code sau khi xử lý
    assertEquals(HttpStatus.TOO_MANY_REQUESTS, exchange3.getResponse().getStatusCode());
  }

  @Test
  void whenDifferentIps_thenSeparateBuckets(){
    RateLimiterGatewayFilterFactory.Config config = new RateLimiterGatewayFilterFactory.Config();
    config.setRouteId("test_route");
    GatewayFilter filter = filterFactory.apply(config);

    // Tạo các exchange riêng biệt
    ServerWebExchange exchange1_1 = createExchange("127.0.0.1");
    ServerWebExchange exchange1_2 = createExchange("127.0.0.1");
    ServerWebExchange exchange1_3 = createExchange("127.0.0.1");

    ServerWebExchange exchange2_1 = createExchange("192.168.1.1");
    ServerWebExchange exchange2_2 = createExchange("192.168.1.1");
    ServerWebExchange exchange2_3 = createExchange("192.168.1.1");

    // IP1: 2 requests (đạt giới hạn)
    filter.filter(exchange1_1, filterChain).block();
    filter.filter(exchange1_2, filterChain).block();

    // IP2: 2 requests (đạt giới hạn)
    filter.filter(exchange2_1, filterChain).block();
    filter.filter(exchange2_2, filterChain).block();

    // Kiểm tra request thứ 3 từ IP1 (vượt giới hạn)
    filter.filter(exchange1_3, filterChain).block();
    assertEquals(HttpStatus.TOO_MANY_REQUESTS, exchange1_3.getResponse().getStatusCode());

    // Kiểm tra request thứ 3 từ IP2 (vượt giới hạn)
    filter.filter(exchange2_3, filterChain).block();
    assertEquals(HttpStatus.TOO_MANY_REQUESTS, exchange2_3.getResponse().getStatusCode());
  }

  private ServerWebExchange createExchange(String ipAddress)
  {
    return MockServerWebExchange.from(
        MockServerHttpRequest.get("/")
            .remoteAddress(new InetSocketAddress(ipAddress, 8080))
    );
  }

  // Bucket giả lập cho test
  public static class TestBucket implements Bucket {
    private long tokens;

    public TestBucket(long tokens) {
      this.tokens = tokens;
    }

    @Override
    public boolean tryConsume(long tokens) {
      if (this.tokens >= tokens) {
        this.tokens -= tokens;
        return true;
      }
      return false;
    }

    // Thêm các phương thức cần thiết
    @Override
    public long getAvailableTokens() {
      return tokens;
    }

    @Override
    public void addTokens(long tokens) {
      this.tokens += tokens;
    }

    @Override
    public void forceAddTokens(long tokens) {
      this.tokens += tokens;
    }

    // Các phương thức khác có thể ném UnsupportedOperationException
    @Override
    public ConsumptionProbe tryConsumeAndReturnRemaining(long tokens) {
      throw new UnsupportedOperationException();
    }

    @Override
    public EstimationProbe estimateAbilityToConsume(long tokens) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void replaceConfiguration(BucketConfiguration newConfiguration, TokensInheritanceStrategy tokensInheritanceStrategy) {
      throw new UnsupportedOperationException();
    }

    // Thêm tất cả phương thức còn thiếu từ interface
    @Override
    public BlockingBucket asBlocking() {
      throw new UnsupportedOperationException();
    }

    @Override
    public SchedulingBucket asScheduler() {
      throw new UnsupportedOperationException();
    }

    @Override
    public VerboseBucket asVerbose() {
      throw new UnsupportedOperationException();
    }

    @Override
    public long consumeIgnoringRateLimits(long tokens) {
      throw new UnsupportedOperationException();
    }

    @Override
    public long tryConsumeAsMuchAsPossible() {
      throw new UnsupportedOperationException();
    }

    @Override
    public long tryConsumeAsMuchAsPossible(long limit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Bucket toListenable(BucketListener listener) {
      throw new UnsupportedOperationException();
    }
  }
}
