package com.codecampus.gateway.configuration.config;

import static com.codecampus.gateway.constant.config.SecurityConfigConstant.URL_PATTERN_ALL;

import com.codecampus.gateway.configuration.filter.RateLimitFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig
{
  /**
   * Đăng ký Filter trong SecurityConfig
   */
  @Bean
  public FilterRegistrationBean<RateLimitFilter> rateLimitFilterRegistration()
  {
    FilterRegistrationBean<RateLimitFilter> bean = new FilterRegistrationBean<>();
    bean.setFilter(new RateLimitFilter());

    // Áp dụng cho tất cả API
    bean.addUrlPatterns(URL_PATTERN_ALL);

    // Chạy trước các filter khác
    bean.setOrder(0);
    return bean;
  }
}
