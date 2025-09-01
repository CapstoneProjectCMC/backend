package com.codecampus.organization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OrgServiceApplication {

  static void main(String[] args) {
    SpringApplication.run(OrgServiceApplication.class, args);
  }

}
