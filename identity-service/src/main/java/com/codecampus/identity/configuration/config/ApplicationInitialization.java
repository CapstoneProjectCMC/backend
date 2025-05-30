package com.codecampus.identity.configuration.config;

import static com.codecampus.identity.constant.authentication.AuthenticationConstant.ADMIN_ROLE;
import static com.codecampus.identity.constant.authentication.AuthenticationConstant.USER_ROLE;

import com.codecampus.identity.entity.account.Permission;
import com.codecampus.identity.entity.account.Role;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.repository.account.PermissionRepository;
import com.codecampus.identity.repository.account.RoleRepository;
import com.codecampus.identity.repository.account.UserRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitialization
{

  RoleRepository roleRepository;
  PermissionRepository permissionRepository;
  UserRepository userRepository;
  PasswordEncoder passwordEncoder;

  @Bean
  @ConditionalOnProperty(
      prefix = "spring.datasource",
      value = "driver-class-name",
      havingValue = "org.postgresql.Driver")
  ApplicationRunner applicationRunner()
  {
    log.info("Khởi chạy ứng dụng!");
    return args -> {
      if (userRepository.findByUsername("admin").isEmpty()) {
        roleRepository.save(Role.builder()
            .name(USER_ROLE)
            .description("User role")
            .build());

        Role adminRole = roleRepository.save(Role.builder()
            .name(ADMIN_ROLE)
            .description("Admin role")
            .build());

        var roles = new HashSet<Role>();
        roles.add(adminRole);

        User user = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin123"))
            .email("admin123123123@mail.com")
            .enabled(true)
            .roles(roles)
            .build();

        userRepository.save(user);
        log.warn("admin user has been created with default password: admin, please change it");
      }
      log.info("Application initialization completed .....");
    };  }
}