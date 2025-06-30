package com.codecampus.identity.configuration.config.init;

import com.codecampus.identity.repository.account.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitialization
{
  UserRepository userRepository;
  ApplicationInitializationService applicationInitializationService;

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.datasource",
      value = "driver-class-name",
      havingValue = "org.postgresql.Driver")
  ApplicationRunner applicationRunner()
  {
    log.info("Khởi chạy ứng dụng!");
    return args -> {

      if (userRepository.findByUsername("admin").isEmpty())
      {
        applicationInitializationService.createAdminUser(
            "admin",
            "admin123",
            "admin123123123@mail.com"
        );
      }

      if (userRepository.findByUsername("code_campusadmin").isEmpty())
      {
        applicationInitializationService.createAdminUser(
            "code_campusadmin",
            "Coco2025@2025@2025",
            "code_campusadmin@mail.com"
        );
      }

      if (userRepository.findByUsername("yunomix2834").isEmpty())
      {
        applicationInitializationService.createUser(
            "yunomix2834",
            "dinhanst2832004",
            "yunomix2834@mail.com"
        );
      }

      log.info("khởi chạy ứng dụng thành công .....");
    };
  }
}