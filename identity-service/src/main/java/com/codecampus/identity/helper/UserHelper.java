package com.codecampus.identity.helper;

import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.exception.AppException;
import com.codecampus.identity.exception.ErrorCode;
import com.codecampus.identity.repository.account.UserRepository;
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
}
