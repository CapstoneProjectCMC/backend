package com.codecampus.chat.configuration.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;

public class FeignMultipartConfiguration {
  @Bean
  public Encoder feignFormEncoder(
      ObjectFactory<HttpMessageConverters> converters) {
    return new SpringFormEncoder();
  }
}
