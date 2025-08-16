package com.codecampus.post.service.FeignConfig;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignMultipartSupportConfig  {
    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder();
    }
}
