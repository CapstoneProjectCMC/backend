package com.codecampus.post;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableFeignClients
@EnableKafka
@EnableAsync
public class PostServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(PostServiceApplication.class, args);
  }

}
