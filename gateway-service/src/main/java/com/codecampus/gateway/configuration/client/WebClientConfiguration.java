package com.codecampus.gateway.configuration.client;

import com.codecampus.gateway.repository.client.IdentityClient;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Lớp cấu hình các bean liên quan đến WebClient và CORS cho Gateway.
 * Bao gồm:
 * <ul>
 *   <li>Khởi tạo WebClient để kết nối đến Identity Service.</li>
 *   <li>Thiết lập bộ lọc CORS cho tất cả endpoint.</li>
 *   <li>Đăng ký bean IdentityClient để gọi các API từ Identity Service.</li>
 * </ul>
 */
@Configuration
public class WebClientConfiguration {

  /**
   * Tạo và cấu hình bean WebClient với URL cơ sở được định nghĩa trong hằng số.
   *
   * @return một đối tượng WebClient dùng để thực hiện các HTTP request đến Identity Service
   */
  @Bean
  WebClient webClient(IdentityServiceProperties props) {
    return WebClient.builder()
        .baseUrl(props.getBaseUrl())
        .build();
  }

  /**
   * Tạo và cấu hình bộ lọc CORS (Cross-Origin Resource Sharing) cho toàn bộ API.
   * Cho phép:
   * <ul>
   *   <li>Chấp nhận credentials trong yêu cầu.</li>
   *   <li>Cho phép tất cả nguồn gốc (origin).</li>
   *   <li>Cho phép tất cả header và phương thức HTTP.</li>
   * </ul>
   *
   * @return một đối tượng CorsWebFilter áp dụng cấu hình CORS cho mọi đường dẫn
   */
  @Bean
  CorsWebFilter corsWebFilter() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowCredentials(true);
    corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));
    corsConfiguration.setAllowedHeaders(List.of("*"));
    corsConfiguration.setAllowedMethods(List.of("*"));

    UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource =
        new UrlBasedCorsConfigurationSource();
    urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",
        corsConfiguration);

    return new CorsWebFilter(urlBasedCorsConfigurationSource);
  }

  /**
   * Đăng ký bean IdentityClient sử dụng HttpServiceProxyFactory dựa trên WebClient đã khởi tạo.
   * IdentityClient sẽ được sinh tự động proxy để gọi các endpoint của Identity Service.
   *
   * @param webClient WebClient đã cấu hình để kết nối đến Identity Service
   * @return một đối tượng IdentityClient để thực hiện các cuộc gọi HTTP đến Identity Service
   */
  @Bean
  IdentityClient identityClient(WebClient webClient) {
    HttpServiceProxyFactory httpServiceProxyFactory =
        HttpServiceProxyFactory
            .builderFor(WebClientAdapter.create(webClient))
            .build();

    return httpServiceProxyFactory.createClient(IdentityClient.class);
  }
}
