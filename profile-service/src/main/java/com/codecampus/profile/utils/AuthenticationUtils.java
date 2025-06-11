package com.codecampus.profile.utils;


import com.codecampus.profile.exception.AppException;
import com.codecampus.profile.exception.ErrorCode;
import com.codecampus.profile.repository.UserProfileRepository;
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
  UserProfileRepository userProfileRepository;

  public void checkExistsUserid(String userId)
  {
    if (userProfileRepository.existsByUserId(userId))
    {
      throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
    }
  }

}
