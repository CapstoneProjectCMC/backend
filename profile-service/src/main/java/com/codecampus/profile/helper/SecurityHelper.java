package com.codecampus.profile.helper;

import com.codecampus.profile.exception.AppException;
import com.codecampus.profile.exception.ErrorCode;
import com.codecampus.profile.repository.UserProfileRepository;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Tiện ích hỗ trợ lấy thông tin người dùng hiện đang đăng nhập
 * từ ngữ cảnh bảo mật của Spring Security.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityHelper
{
  UserProfileRepository userProfileRepository;

  /**
   * Lấy ID của người dùng đã đăng nhập.
   *
   * @return chuỗi tên đăng nhập hoặc null nếu chưa xác thực
   */
  public static String getMyUserId()
  {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated())
    {
      return null;
    }

    Object principal = auth.getPrincipal();

    if (principal instanceof Jwt jwt)
    {
      // JwtAuthenticationToken giữ nguyên đối tượng Jwt làm principal,
      return jwt.getClaimAsString("userId");
    }

    return null;
  }

  public static String getMyUsername()
  {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated())
    {
      return null;
    }

    Object principal = auth.getPrincipal();

    if (principal instanceof Jwt jwt)
    {
      // JwtAuthenticationToken giữ nguyên đối tượng Jwt làm principal,
      return jwt.getClaimAsString("username");
    }

    return null;
  }

  public static String getMyEmail()
  {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return (auth == null) ? null : auth.getName();
  }

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
