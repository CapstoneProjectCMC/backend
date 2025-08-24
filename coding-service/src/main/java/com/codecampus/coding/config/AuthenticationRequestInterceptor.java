package com.codecampus.coding.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign RequestInterceptor để tự động thêm header Authorization
 * của HTTP request hiện tại vào mọi request gửi đi qua Feign client.
 *
 * <p>Khi có ServletRequestAttributes từ RequestContextHolder,
 * lấy header "Authorization" từ HttpServletRequest và
 * thêm vào RequestTemplate; nếu header rỗng, bỏ qua.</p>
 */
@Slf4j
public class AuthenticationRequestInterceptor
    implements RequestInterceptor {
  /**
   * Phương thức được gọi trước khi Feign gửi request.
   *
   * <ol>
   *   <li>Lấy ServletRequestAttributes từ RequestContextHolder.</li>
   *   <li>Nếu tồn tại, lấy HttpServletRequest và trích header "Authorization".</li>
   *   <li>Nếu header có giá trị, thêm vào RequestTemplate của Feign.</li>
   * </ol>
   *
   * @param requestTemplate template của request chuẩn bị gửi đi
   */
  @Override
  public void apply(RequestTemplate requestTemplate) {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    var authHeader = attributes.getRequest().getHeader("Authorization");

    log.info("Header: {}", authHeader);
    if (StringUtils.hasText(authHeader)) {
      requestTemplate.header("Authorization", authHeader);
    }
  }
}
