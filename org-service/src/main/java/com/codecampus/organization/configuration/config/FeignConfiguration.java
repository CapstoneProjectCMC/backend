package com.codecampus.organization.configuration.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình cho Feign client để hỗ trợ gửi dữ liệu dạng multipart/form-data.
 *
 * <p>Bean {@link Encoder} được đăng ký sử dụng
 * {@link SpringFormEncoder} giúp Feign mã hóa các request
 * có nội dung form hoặc file upload một cách tự động.
 * </p>
 */
@Configuration
public class FeignConfiguration {
  /**
   * Đăng ký {@link Encoder} tùy chỉnh cho Feign,
   * sử dụng {@link SpringFormEncoder} để mã hóa multipart/form-data.
   *
   * @return đối tượng Encoder hỗ trợ form encoding
   */
  @Bean
  public Encoder multipartFormEncoder() {
    return new SpringFormEncoder();
  }
}
