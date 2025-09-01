package com.codecampus.quiz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class QuizApplication {

  static void main(String[] args) {
    SpringApplication.run(QuizApplication.class, args);
  }

}
