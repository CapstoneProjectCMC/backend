package com.codecampus.gateway.configuration.filter;

import static com.codecampus.gateway.constant.config.SecurityConfigConstant.PUBLIC_ENDPOINTS;

import com.codecampus.gateway.dto.api.ApiResponse;
import com.codecampus.gateway.exception.ErrorCode;
import com.codecampus.gateway.service.IdentityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Lọc toàn cục để xác thực JWT token cho các request đi qua Gateway.
 * Bỏ qua các đường dẫn công khai (public).
 */
@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class AuthenticationFilter
    implements GlobalFilter, Ordered {

  IdentityService identityService;
  ObjectMapper objectMapper;

  @Value("${app.api-prefix}")
  @NonFinal
  private String apiPrefix;

  /**
   * Thực hiện lọc cho mỗi request:
   * <ul>
   *   <li>Cho phép request đến các public endpoints mà không cần xác thực.</li>
   *   <li>Lấy header Authorization, kiểm tra token hợp lệ qua IdentityService.</li>
   *   <li>Chuyển tiếp hoặc trả về 401 nếu không xác thực được.</li>
   * </ul>
   *
   * @param exchange chứa thông tin request và response
   * @param chain    chuỗi filter tiếp theo
   * @return Mono hoàn thành khi xử lý xong
   */
  @Override
  public Mono<Void> filter(
      ServerWebExchange exchange,
      GatewayFilterChain chain) {

    log.info("Enter authentication filter....");

    if (isPublicEndpoint(exchange.getRequest())) {
      return chain.filter(exchange);
    }

    // Get token from authorization header
    List<String> authHeader = exchange.getRequest()
        .getHeaders().get(HttpHeaders.AUTHORIZATION);
    if (CollectionUtils.isEmpty(authHeader)) {
      return unauthenticated(exchange.getResponse());
    }

    String token = authHeader.getFirst().replace("Bearer ", "");
    log.info("TOKEN: {}", token);

    return identityService.introspect(token)
        .flatMap(introspectResponse -> {
          if (introspectResponse.getResult().isValid()) {
            return chain.filter(exchange);
          } else {
            return unauthenticated(exchange.getResponse());
          }
        })
        .onErrorResume(
            throwable -> unauthenticated(exchange.getResponse()));
  }

  /**
   * Thiết lập thứ tự chạy của filter.
   * Giá trị negative để chạy trước các filter khác.
   *
   * @return thứ tự ưu tiên
   */
  @Override
  public int getOrder() {
    return -1;
  }

  /**
   * Kiểm tra xem request có phải endpoint công khai không.
   *
   * @param request đối tượng ServerHttpRequest
   * @return true nếu endpoint nằm trong danh sách PUBLIC_ENDPOINTS
   */
  private boolean isPublicEndpoint(
      ServerHttpRequest request) {
    return Arrays.stream(PUBLIC_ENDPOINTS)
        .anyMatch(s -> request.getURI()
            .getPath()
            .matches(apiPrefix + s)
        );
  }

  /**
   * Xử lý trường hợp không xác thực thành công, trả về 401 với nội dung JSON.
   *
   * @param response đối tượng ServerHttpResponse
   * @return Mono hoàn thành khi ghi response
   */
  Mono<Void> unauthenticated(
      ServerHttpResponse response) {
    ApiResponse<?> apiResponse = ApiResponse.builder()
        .code(ErrorCode.UNAUTHENTICATED.getCode())
        .message(ErrorCode.UNAUTHENTICATED.getMessage())
        .status(ErrorCode.UNAUTHENTICATED.getStatus())
        .build();

    String body;

    try {
      body = objectMapper.writeValueAsString(apiResponse);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    response.setStatusCode(HttpStatus.UNAUTHORIZED);
    response.getHeaders().add(
        HttpHeaders.CONTENT_TYPE,
        MediaType.APPLICATION_JSON_VALUE
    );

    return response.writeWith(
        Mono.just(response.bufferFactory().wrap(body.getBytes()))
    );
  }
}
