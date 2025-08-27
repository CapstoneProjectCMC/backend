package com.codecampus.identity.configuration.config.init;

import com.codecampus.identity.entity.account.Role;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.repository.account.RoleRepository;
import com.codecampus.identity.repository.account.UserRepository;
import com.codecampus.identity.service.kafka.UserEventProducer;
import com.codecampus.identity.utils.ConvertUtils;
import events.user.data.UserProfileCreationPayload;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitializationService {

  RoleRepository roleRepository;
  UserRepository userRepository;
  PasswordEncoder passwordEncoder;
  UserEventProducer userEventProducer;

  @Transactional
  void createUserIfProvided(
      String username,
      String roleName,
      String rawPassword,
      String email) {

    if (!StringUtils.hasText(username)) {
      return;
    }

    if (userRepository.findByUsername(username).isPresent()) {
      return;
    }

    Role role = checkRoleAndCreate(roleName);

    User user = userRepository.save(User.builder()
        .username(username)
        .password(passwordEncoder.encode(rawPassword))
        .email(email)
        .enabled(true)
        .roles(Set.of(role))
        .build());

    UserProfileCreationPayload payload =
        UserProfileCreationPayload.builder()
            .firstName(roleName.charAt(0) + roleName.substring(1).toLowerCase())
            .lastName("Sample")
            .displayName(roleName + " Sample")
            .dob(ConvertUtils.parseDdMmYyyyToInstant("28/03/2004"))
            .bio("Too lazy to write anything :v")
            .gender(true)
            .education(11)
            .links(new String[] {"https://github.com/yunomix2834",
                "https://github.com/CapstoneProjectCMC/backend"})
            .city("Vietnam")
            .build();

    userEventProducer.publishRegisteredUserEvent(user, payload);
  }

  Role checkRoleAndCreate(String roleName) {
    return roleRepository.findById(roleName).orElseGet(() ->
        roleRepository.save(
            Role.builder()
                .name(roleName)
                .description(roleName)
                .build())
    );
  }
}
