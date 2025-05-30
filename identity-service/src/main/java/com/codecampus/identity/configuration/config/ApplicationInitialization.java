package com.codecampus.identity.configuration.config;

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
    return this::run;
  }

  /**
   * Code chạy các thành phần khởi tạo ở đây
   */
  private void run(ApplicationArguments args)
  {
    createDefaultRolesAndPermissions();
  }

  private void createDefaultRolesAndPermissions()
  {
    // Tạo các permission cơ bản
    Permission userPermission =
        createPermissionIfNotFound("USER_PERMISSION");
    Permission adminPermission =
        createPermissionIfNotFound("ADMIN_PERMISSION");

    // Tạo role USER với các permission cơ bản
    Role userRole = createRoleIfNotFound("ROLE_USER", Set.of(userPermission));
    Role adminRole = createRoleIfNotFound("ROLE_ADMIN",
        Set.of(userPermission, adminPermission));

    // Tạo user ADMIN
    createUserIfNotFound(
        "admin",
        "yunomix2834@gmail.com",
        "admin123",
        Set.of(adminRole, userRole)
    );

    createUserIfNotFound(
        "user",
        "yunomix280304@gmail.com",
        "user123",
        Set.of(userRole)
    );
  }

  private Permission createPermissionIfNotFound(String name)
  {
    return permissionRepository
        .findByName(name)
        .orElseGet(
            () -> permissionRepository.save(Permission.builder()
                .name(name)
                .build())
        );
  }

  private Role createRoleIfNotFound(
      String name,
      Set<Permission> permissions)
  {
    return roleRepository
        .findByName(name)
        .orElseGet(() -> roleRepository.save(Role.builder()
            .name(name)
            .permissions(permissions)
            .build()));
  }

  private void createUserIfNotFound(
      String username,
      String email,
      String password,
      Set<Role> roles) {

    userRepository
        .findByUsername(username)
        .orElseGet(() -> userRepository.save(User.builder()
            .username(username)
            .email(email)
            .password(passwordEncoder.encode(password))
            .roles(new HashSet<>())
            .build()));
  }
}