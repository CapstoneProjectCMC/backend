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

/**
 * Tiện ích hỗ trợ xác thực liên quan đến người dùng.
 *
 * <p>Dùng để kiểm tra tính hợp lệ ban đầu khi thao tác với User,
 *      như kiểm tra xem username hoặc email đã tồn tại hay chưa.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationUtils
{
  UserRepository userRepository;

  /**
   * Kiểm tra xem username hoặc email đã tồn tại trong hệ thống hay chưa.
   *
   * <p>Nếu username đã tồn tại, ném AppException với mã lỗi USER_ALREADY_EXISTS.
   * Nếu email đã tồn tại, ném AppException với mã lỗi EMAIL_ALREADY_EXISTS.</p>
   *
   * @param username tên đăng nhập cần kiểm tra
   * @param email địa chỉ email cần kiểm tra
   * @throws AppException khi username hoặc email đã tồn tại
   */
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
