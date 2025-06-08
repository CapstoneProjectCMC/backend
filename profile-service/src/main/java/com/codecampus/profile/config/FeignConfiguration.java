package com.codecampus.profile.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration
{
  @Bean
  public Encoder multipartFormEncoder() {
    return new SpringFormEncoder();
  }
}
