package com.codecampus.coding.config.grpc;

import com.fasterxml.jackson.databind.Module;
import com.hubspot.jackson.datatype.protobuf.ProtobufModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Giúp Jackson (Spring MVC) hiểu các class được generate bởi protobuf
 * Khi Spring Boot khởi tạo, tất cả Jackson2ObjectMapperBuilderCustomizer
 * sẽ được áp dụng cho *mọi* ObjectMapper (cả MVC, WebClient, Actuator…).
 */
@Configuration
public class JacksonProtobufConfig {

  @Bean
  // Spring Boot tự động thêm mọi Module bean vào ObjectMapper mặc định
  public Module protobufModule() {
    return new ProtobufModule();
  }
}
