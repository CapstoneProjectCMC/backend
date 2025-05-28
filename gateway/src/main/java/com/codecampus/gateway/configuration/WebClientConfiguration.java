// package com.codecampus.gateway.configuration;

// import static com.codecampus.gateway.constant.config.SecurityConfigConstant.IDENTITY_SERVICE_ENDPOINT;

// import java.util.List;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.reactive.CorsWebFilter;
// import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
// import org.springframework.web.reactive.function.client.WebClient;

// @Configuration
// public class WebClientConfiguration {

//   @Bean
//   WebClient webClient() {
//     return WebClient.builder()
//         .baseUrl(IDENTITY_SERVICE_ENDPOINT)
//         .build();
//   }

//   @Bean
//   CorsWebFilter corsWebFilter() {
//     CorsConfiguration corsConfiguration = new CorsConfiguration();
//     corsConfiguration.setAllowedOrigins(List.of("*"));
//     corsConfiguration.setAllowedHeaders(List.of("*"));
//     corsConfiguration.setAllowedMethods(List.of("*"));

//     UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
//     urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

//     return new CorsWebFilter(urlBasedCorsConfigurationSource);
//   }
// }
