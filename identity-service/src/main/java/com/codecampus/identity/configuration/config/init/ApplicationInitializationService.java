package com.codecampus.identity.configuration.config.init;

import com.codecampus.identity.dto.request.profile.UserProfileCreationRequest;
import com.codecampus.identity.entity.account.Role;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.repository.account.PermissionRepository;
import com.codecampus.identity.repository.account.RoleRepository;
import com.codecampus.identity.repository.account.UserRepository;
import com.codecampus.identity.repository.httpclient.profile.ProfileClient;
import com.codecampus.identity.utils.ConvertUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static com.codecampus.identity.constant.authentication.AuthenticationConstant.ADMIN_ROLE;
import static com.codecampus.identity.constant.authentication.AuthenticationConstant.USER_ROLE;

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
    void createAdminUser(String username, String password, String email) {
        Role adminRole = checkRoleAndCreate(ADMIN_ROLE);

        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);

        User user = userRepository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .enabled(true)
                .roles(roles)
                .build());

        profileClient.internalCreateUserProfile(
                UserProfileCreationRequest.builder()
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

    @Transactional
    void createUser(String username, String password, String email) {
        Role userRole = checkRoleAndCreate(USER_ROLE);

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = userRepository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .enabled(true)
                .roles(roles)
                .build());

        profileClient.internalCreateUserProfile(
                UserProfileCreationRequest.builder()
                        .userId(user.getId())
                        .firstName("Code")
                        .lastName("Campus")
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
        return roleRepository.findByName(roleName);
    }
}
