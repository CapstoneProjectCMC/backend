package com.codecampus.identity.configuration.config.init;

import static com.codecampus.identity.constant.authentication.AuthenticationConstant.ADMIN_ROLE;
import static com.codecampus.identity.constant.authentication.AuthenticationConstant.STUDENT_ROLE;
import static com.codecampus.identity.constant.authentication.AuthenticationConstant.TEACHER_ROLE;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitialization {

  ApplicationInitializationService applicationInitializationService;
  Environment env;

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.datasource",
      value = "driver-class-name",
      havingValue = "org.postgresql.Driver")
  ApplicationRunner applicationRunner() {
    log.info("Khởi chạy ứng dụng!");
    return args -> {

      applicationInitializationService.checkRoleAndCreate(ADMIN_ROLE);
      applicationInitializationService.checkRoleAndCreate(TEACHER_ROLE);
      applicationInitializationService.checkRoleAndCreate(STUDENT_ROLE);


      // Đọc từ env/properties
      applicationInitializationService.createUserIfProvided(
          env.getProperty("app.init.admin.username"),
          ADMIN_ROLE,
          env.getProperty("app.init.admin.password"),
          env.getProperty("app.init.admin.email")
      );
      applicationInitializationService.createUserIfProvided(
          env.getProperty("app.init.teacher.username"),
          TEACHER_ROLE,
          env.getProperty("app.init.teacher.password"),
          env.getProperty("app.init.teacher.email")
      );
      applicationInitializationService.createUserIfProvided(
          env.getProperty("app.init.student.username"),
          STUDENT_ROLE,
          env.getProperty("app.init.student.password"),
          env.getProperty("app.init.student.email")
      );

      log.info("khởi chạy ứng dụng thành công .....");
    };
  }
}