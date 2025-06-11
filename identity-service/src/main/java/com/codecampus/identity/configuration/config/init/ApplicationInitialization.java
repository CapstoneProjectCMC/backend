package com.codecampus.identity.configuration.config.init;

import static com.codecampus.identity.constant.authentication.AuthenticationConstant.ADMIN_ROLE;
import static com.codecampus.identity.constant.authentication.AuthenticationConstant.USER_ROLE;

import com.codecampus.identity.entity.account.Role;
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

      Role adminRole = applicationInitializationService.checkRoleAndCreate(ADMIN_ROLE);
      applicationInitializationService.checkRoleAndCreate(USER_ROLE);

      if (userRepository.findByUsername("admin").isEmpty()) {

        applicationInitializationService.createAdminUser(adminRole);

        log.warn("admin user has been created with default password: admin, please change it");
      }
      log.info("khởi chạy ứng dụng thành công .....");
    };
  }
}