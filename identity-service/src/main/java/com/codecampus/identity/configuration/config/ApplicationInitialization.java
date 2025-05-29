package com.codecampus.identity.configuration.config;

import com.codecampus.identity.entity.account.Permission;
import com.codecampus.identity.entity.account.Role;
import com.codecampus.identity.repository.account.PermissionRepository;
import com.codecampus.identity.repository.account.RoleRepository;
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

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitialization
{

  RoleRepository roleRepository;
  PermissionRepository permissionRepository;

  @Bean
  @ConditionalOnProperty(prefix = "")
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
    createRoleIfNotFound("ROLE_USER", Set.of(userPermission));
    createRoleIfNotFound("ROLE_ADMIN",
        Set.of(userPermission, adminPermission));
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

  private void createRoleIfNotFound(
      String name,
      Set<Permission> permissions)
  {
    roleRepository
        .findByName(name)
        .orElseGet(() -> roleRepository.save(Role.builder()
            .name(name)
            .permissions(permissions)
            .build()));
  }
}