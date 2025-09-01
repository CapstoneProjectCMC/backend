package com.codecampus.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class NotificationApplication {

  static void main(String[] args) {
    SpringApplication.run(NotificationApplication.class, args);
  }

}
