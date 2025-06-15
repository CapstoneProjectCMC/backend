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

/**
 * Tiện ích hỗ trợ xác thực liên quan đến hồ sơ người dùng (UserProfile).
 *
 * <p>Cung cấp phương thức kiểm tra trước khi tạo mới hồ sơ,
 * đảm bảo userId chưa tồn tại trong hệ thống.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationUtils
{
  UserProfileRepository userProfileRepository;

  /**
   * Kiểm tra xem userId đã tồn tại trong hệ thống hay chưa.
   *
   * @param userId mã định danh của người dùng cần kiểm tra
   * @throws AppException nếu userId đã tồn tại (ErrorCode.USER_ALREADY_EXISTS)
   */
  public void checkExistsUserid(String userId)
  {
    if (userProfileRepository.existsByUserId(userId))
    {
      throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
    }
  }

}
