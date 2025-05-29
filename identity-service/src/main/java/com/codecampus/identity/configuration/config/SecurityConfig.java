package com.codecampus.identity.configuration.config;

import static com.codecampus.identity.constant.config.SecurityConfigConstant.ACCEPT_HEADER;
import static com.codecampus.identity.constant.config.SecurityConfigConstant.AUTHORIZATION_HEADER;
import static com.codecampus.identity.constant.config.SecurityConfigConstant.CONTENT_TYPE_HEADER;
import static com.codecampus.identity.constant.config.SecurityConfigConstant.DELETE_METHOD;
import static com.codecampus.identity.constant.config.SecurityConfigConstant.FRONTEND_ENDPOINT;
import static com.codecampus.identity.constant.config.SecurityConfigConstant.FRONTEND_ENDPOINT2;
import static com.codecampus.identity.constant.config.SecurityConfigConstant.FRONTEND_ENDPOINT3;
import static com.codecampus.identity.constant.config.SecurityConfigConstant.GET_METHOD;
import static com.codecampus.identity.constant.config.SecurityConfigConstant.OPTIONS_METHOD;
import static com.codecampus.identity.constant.config.SecurityConfigConstant.PATCH_METHOD;
import static com.codecampus.identity.constant.config.SecurityConfigConstant.POST_METHOD;
import static com.codecampus.identity.constant.config.SecurityConfigConstant.PUBLIC_ENDPOINTS;
import static com.codecampus.identity.constant.config.SecurityConfigConstant.PUT_METHOD;
import static com.codecampus.identity.constant.config.SecurityConfigConstant.URL_PATTERN_ALL;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig
{

  JwtAuthenticationConverter jwtAuthenticationConverter;

  @Value("${app.jwt.key-secret}")
  @NonFinal
  String jwtKeySecret;

  /**
   * Cấu hình SecurityFilterChain cho ứng dụng.
   * <p>
   * Cấu hình này thực hiện các bước sau:
   * <ul>
   *   <li>Kích hoạt CORS với cấu hình mặc định.</li>
   *   <li>Tắt CSRF (vì đang sử dụng JWT).</li>
   *   <li>Thiết lập session management dưới dạng stateless.</li>
   *   <li>Cho phép truy cập các endpoint công khai và yêu cầu xác thực cho các endpoint còn lại.</li>
   *   <li>Gắn bộ lọc JWT trước bộ lọc xác thực mặc định.</li>
   *   <li>Sử dụng cấu hình HTTP Basic tạm thời.</li>
   * </ul>
   * </p>
   *
   * @param http đối tượng HttpSecurity để cấu hình bảo mật
   * @return SecurityFilterChain đã được cấu hình
   * @throws Exception nếu có lỗi xảy ra trong quá trình cấu hình
   */
  @Bean
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http) throws Exception
  {
    http
        // Kích hoạt CORS với cấu hình mặc định
        .cors(Customizer.withDefaults())

        // Tắt CSRF vì sử dụng JWT
        .csrf(AbstractHttpConfigurer::disable)

        // Cấu hình session theo dạng stateless
        .sessionManagement(session -> session.sessionCreationPolicy(
            SessionCreationPolicy.STATELESS))

        // Cho phép public với các PUBLIC ENDPOINT
        // Và yêu cầu xác thực cho các request khác
        .authorizeHttpRequests(auth -> {
          auth.requestMatchers(PUBLIC_ENDPOINTS).permitAll();
          auth.anyRequest().authenticated();
        })

        .oauth2ResourceServer(oauth2 -> oauth2.jwt(
            jwt -> jwt
                .jwtAuthenticationConverter(
                    jwtAuthenticationConverter)
                .decoder(jwtDecoder())
        ))
        // Logout handler
        .logout(logout -> logout
            .logoutUrl("/auth/logout")
            .logoutSuccessHandler((request, response, authentication) -> {
              SecurityContextHolder.clearContext();
              response.setStatus(HttpStatus.OK.value());
            })
            .addLogoutHandler((request, response, authentication) -> {
                
            })
        )
    ;

    return http.build();
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
  public CorsConfigurationSource corsConfigurationSource()
  {
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

  @Bean
  public JwtDecoder jwtDecoder()
  {
    try
    {
      return NimbusJwtDecoder
          .withPublicKey(publicKey())
          .signatureAlgorithm(SignatureAlgorithm.RS256)
          .build();
    } catch (Exception e)
    {
      throw new RuntimeException("Failed to create JWT Decoder", e);
    }
  }

  @Bean
  public JwtEncoder jwtEncoder(KeyPair keyPair)
  {
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

    RSAKey rsaKey = new RSAKey.Builder(publicKey)
        .privateKey(privateKey)
        .keyID(jwtKeySecret)
        .build();

    JWKSource<SecurityContext> jwkSource =
        new ImmutableJWKSet<>(new JWKSet(rsaKey));
    return new NimbusJwtEncoder(jwkSource);
  }

  @Bean
  public RSAPublicKey publicKey()
  {
    return (RSAPublicKey) keyPair().getPublic();
  }

  @Bean
  public KeyPair keyPair()
  {
    try
    {
      KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
      generator.initialize(2048);
      return generator.generateKeyPair();
    } catch (Exception e)
    {
      throw new RuntimeException("Failed to create RSA KeyPair", e);
    }
  }
}
