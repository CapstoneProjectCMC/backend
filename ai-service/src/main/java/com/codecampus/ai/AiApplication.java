package com.codecampus.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AiApplication {

  static void main(String[] args) {
    SpringApplication.run(AiApplication.class, args);
  }
}
