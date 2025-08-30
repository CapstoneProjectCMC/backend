package com.codecampus.identity.helper;

import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.exception.AppException;
import com.codecampus.identity.exception.ErrorCode;
import com.codecampus.identity.repository.account.UserRepository;
import java.util.Locale;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserHelper {

  UserRepository userRepository;

  /**
   * Tìm entity User theo ID.
   *
   * @param id ID người dùng
   * @return User entity
   * @throws AppException nếu không tìm thấy
   */
  public User getUserById(String id) {
    return userRepository.findById(id)
        .orElseThrow(
            () -> new AppException(ErrorCode.USER_NOT_FOUND));
  }

  public String normalizeOrgRole(String role) {
    if (role == null || role.isBlank()) {
      return "STUDENT";
    }
    return switch (role.trim().toUpperCase(Locale.ROOT)) {
      case "ADMIN", "TEACHER", "STUDENT" ->
          role.trim().toUpperCase(Locale.ROOT);
      default -> "STUDENT";
    };
  }

  public UserCreationRequest ensurePassword(UserCreationRequest u) {
    if (u.getPassword() == null || u.getPassword().isBlank()) {
      return UserCreationRequest.builder()
          .username(u.getUsername())
          .email(u.getEmail())
          .password("Mật khẩu ban đầu 123") // dùng tạm nếu không gửi password
          .firstName(u.getFirstName())
          .lastName(u.getLastName())
          .dob(u.getDob())
          .bio(u.getBio())
          .gender(u.isGender())
          .displayName(u.getDisplayName())
          .education(u.getEducation())
          .links(u.getLinks())
          .city(u.getCity())
          .organizationId(u.getOrganizationId())
          .organizationMemberRole(u.getOrganizationMemberRole())
          .build();
    }
    return u;
  }
}
