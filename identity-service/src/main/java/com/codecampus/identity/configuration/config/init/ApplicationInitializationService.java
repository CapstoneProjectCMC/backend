package com.codecampus.identity.configuration.config.init;

import com.codecampus.identity.dto.request.profile.UserProfileCreationRequest;
import com.codecampus.identity.entity.account.Role;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.repository.account.PermissionRepository;
import com.codecampus.identity.repository.account.RoleRepository;
import com.codecampus.identity.repository.account.UserRepository;
import com.codecampus.identity.repository.httpclient.profile.ProfileClient;
import com.codecampus.identity.utils.ConvertUtils;
import java.util.HashSet;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitializationService {
  RoleRepository roleRepository;
  PermissionRepository permissionRepository;
  UserRepository userRepository;
  PasswordEncoder passwordEncoder;
  ProfileClient profileClient;

  @Transactional
  void createAdminUser(Role adminRole) {
    var roles = new HashSet<Role>();
    roles.add(adminRole);

    User user = userRepository.save(User.builder()
        .username("admin")
        .password(passwordEncoder.encode("admin123"))
        .email("admin123123123@mail.com")
        .enabled(true)
        .roles(roles)
        .build());

    profileClient.createUserProfile(UserProfileCreationRequest.builder()
        .userId(user.getId())
        .firstName("Admin")
        .lastName("Sys")
        .dob(ConvertUtils.parseDdMmYyyyToInstant("28/03/2004"))
        .bio("Too lazy to write anything :v")
        .gender(true)
        .displayName("ADMIN SYS")
        .education(11)
        .links(new String[] {"https://github.com/yunomix2834",
            "https://github.com/CapstoneProjectCMC/backend"})
        .city("Vietnam")
        .build()
    );
  }

  Role checkRoleAndCreate(String roleName) {
    if (!roleRepository.existsByName(roleName)) {
      return roleRepository.save(Role.builder()
          .name(roleName)
          .description(roleName)
          .build());
    }
    return null;
  }
}
