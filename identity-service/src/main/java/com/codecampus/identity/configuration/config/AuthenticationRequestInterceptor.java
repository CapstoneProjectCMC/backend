package com.codecampus.identity.configuration.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * RequestInterceptor dùng để truyền header Authorization từ HTTP request gốc
 * vào trong các request được tạo bởi Feign client.
 * Trường hợp không có HTTP request hiện tại (ví dụ chạy trong ApplicationRunner), interceptor sẽ bỏ qua.
 */
@Slf4j
public class AuthenticationRequestInterceptor
    implements RequestInterceptor {
  /**
   * Gắn header Authorization vào RequestTemplate nếu có trong HttpServletRequest.
   *
   * @param requestTemplate đối tượng RequestTemplate chuẩn bị gửi qua Feign
   */
  @Override
  public void apply(RequestTemplate requestTemplate) {
    RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
    if (!(attrs instanceof ServletRequestAttributes)) {
      // Không có HTTP request (ví dụ ApplicationRunner), bỏ qua
      return;
    }

    HttpServletRequest request =
        ((ServletRequestAttributes) attrs).getRequest();
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    log.info("authHeader: {}", authHeader);
    if (StringUtils.hasText(authHeader)) {
      requestTemplate.header(HttpHeaders.AUTHORIZATION, authHeader);
    }
  }
}
