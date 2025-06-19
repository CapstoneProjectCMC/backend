package com.codecampus.submission;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SubmissionApplication {

  public static void main(String[] args) {
    SpringApplication.run(SubmissionApplication.class, args);
  }

}
