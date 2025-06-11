package com.codecampus.identity.utils;

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
public class AuthenticationUtils
{
  UserRepository userRepository;

  public void checkExistsUsernameEmail(String username, String email)
  {
    // Kiểm tra username và email đã tồn tại
    if (userRepository.existsByUsername(username))
    {
      throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
    }

    if (userRepository.existsByEmail(email))
    {
      throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
    }
  }

}
