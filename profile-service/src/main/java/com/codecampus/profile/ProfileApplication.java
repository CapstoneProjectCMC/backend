package com.codecampus.profile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ProfileApplication {

  static void main(String[] args) {
    SpringApplication.run(ProfileApplication.class, args);
  }

}
