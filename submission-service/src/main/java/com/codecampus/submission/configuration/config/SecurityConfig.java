package com.codecampus.submission.configuration.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.codecampus.submission.constant.config.SecurityConfigConstant.ACCEPT_HEADER;
import static com.codecampus.submission.constant.config.SecurityConfigConstant.AUTHORIZATION_HEADER;
import static com.codecampus.submission.constant.config.SecurityConfigConstant.CONTENT_TYPE_HEADER;
import static com.codecampus.submission.constant.config.SecurityConfigConstant.DELETE_METHOD;
import static com.codecampus.submission.constant.config.SecurityConfigConstant.FRONTEND_ENDPOINT;
import static com.codecampus.submission.constant.config.SecurityConfigConstant.FRONTEND_ENDPOINT2;
import static com.codecampus.submission.constant.config.SecurityConfigConstant.FRONTEND_ENDPOINT3;
import static com.codecampus.submission.constant.config.SecurityConfigConstant.GET_METHOD;
import static com.codecampus.submission.constant.config.SecurityConfigConstant.OPTIONS_METHOD;
import static com.codecampus.submission.constant.config.SecurityConfigConstant.PATCH_METHOD;
import static com.codecampus.submission.constant.config.SecurityConfigConstant.POST_METHOD;
import static com.codecampus.submission.constant.config.SecurityConfigConstant.PUBLIC_ENDPOINTS;
import static com.codecampus.submission.constant.config.SecurityConfigConstant.PUT_METHOD;
import static com.codecampus.submission.constant.config.SecurityConfigConstant.URL_PATTERN_ALL;

/**
 * Cấu hình bảo mật cho Profile Service sử dụng Spring Security.
 *
 * <p>Bao gồm:
 * <ul>
 *   <li>Định nghĩa các endpoint công khai (không yêu cầu xác thực).</li>
 *   <li>Cấu hình OAuth2 Resource Server sử dụng JWT với bộ giải mã tùy chỉnh và entry point xử lý lỗi.</li>
 *   <li>Tắt CSRF để phù hợp với API REST.</li>
 *   <li>Thiết lập CORS cho các origin, method và header được phép.</li>
 *   <li>Cấu hình converter để chuyển đổi JWT claims thành GrantedAuthority.</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    /**
     * Cấu hình SecurityFilterChain cho HTTP security.
     *
     * <p>Thiết lập:
     * <ul>
     *   <li>Cho phép truy cập PUBLIC_ENDPOINTS không cần xác thực.</li>
     *   <li>Các request khác phải xác thực JWT.</li>
     *   <li>Sử dụng customJwtDecoder và jwtAuthenticationConverter để xử lý JWT.</li>
     *   <li>Sử dụng JwtAuthenticationEntryPoint để trả về 401 khi xác thực thất bại.</li>
     *   <li>Tắt CSRF.</li>
     * </ul>
     * </p>
     *
     * @param httpSecurity đối tượng HttpSecurity để cấu hình
     * @return SecurityFilterChain đã cấu hình
     * @throws Exception nếu cấu hình gặp lỗi
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request -> request
                .requestMatchers(PUBLIC_ENDPOINTS)
                .permitAll()
                .anyRequest()
                .authenticated());

        httpSecurity.oauth2ResourceServer(
                oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(customJwtDecoder)
                                .jwtAuthenticationConverter(
                                        jwtAuthenticationConverter()))
                        .authenticationEntryPoint(
                                new JwtAuthenticationEntryPoint()));
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }

    /**
     * Cấu hình CORS cho ứng dụng.
     * <p>
     * Phương thức này thiết lập:
     * <ul>
     *   <li>Các origin được phép truy cập (danh sách FRONTEND_ENDPOINT, FRONTEND_ENDPOINT2, FRONTEND_ENDPOINT3).</li>
     *   <li>Các phương thức HTTP được phép (GET, POST, PUT, DELETE, PATCH, OPTIONS).</li>
     *   <li>Cho phép tất cả các header.</li>
     *   <li>Cho phép gửi credentials.</li>
     *   <li>Các header được expose (ví dụ: "Authorization").</li>
     * </ul>
     * Áp dụng cấu hình này cho tất cả các endpoint.
     * </p>
     *
     * @return CorsConfigurationSource chứa cấu hình CORS của ứng dụng
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Cho phép các origin truy cập định nghĩa sẵn
        configuration.setAllowedOrigins(
                List.of(FRONTEND_ENDPOINT, FRONTEND_ENDPOINT2,
                        FRONTEND_ENDPOINT3)
        );

        // Cho phép các phương thức HTTP được định nghĩa
        configuration.setAllowedMethods(
                List.of(GET_METHOD, POST_METHOD, PUT_METHOD, DELETE_METHOD,
                        PATCH_METHOD, OPTIONS_METHOD));

        // Cho phép tất cả các header
        configuration.setAllowedHeaders(
                List.of(AUTHORIZATION_HEADER, CONTENT_TYPE_HEADER,
                        ACCEPT_HEADER));

        // Cho phép gửi credentials (cookie, header, v.v.)
        configuration.setAllowCredentials(true);

        // Expose header "Authorization"
        configuration.setExposedHeaders(List.of(AUTHORIZATION_HEADER));

        // Thời gian cache preflight request
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        // Áp dụng cấu hình cho tất cả các endpoint
        source.registerCorsConfiguration(URL_PATTERN_ALL, configuration);
        return source;
    }

    /**
     * Tạo {@link JwtAuthenticationConverter} để chuyển đổi JWT claims thành GrantedAuthority.
     * Loại bỏ prefix mặc định (ví dụ "SCOPE_").
     *
     * @return JwtAuthenticationConverter đã cấu hình
     */
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authConverter =
                new JwtGrantedAuthoritiesConverter();
        authConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authConverter);

        // Principal = claim "username"
        converter.setPrincipalClaimName("username");

        return converter;
    }
}
