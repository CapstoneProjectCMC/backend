package com.codecampus.identity.utils;

import com.codecampus.identity.entity.account.User;
import com.codecampus.identity.exception.AppException;
import com.codecampus.identity.exception.ErrorCode;
import com.codecampus.identity.repository.account.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Tiện ích hỗ trợ lấy thông tin người dùng hiện đang đăng nhập
 * từ ngữ cảnh bảo mật của Spring Security.
 */
public class SecurityUtils {
  /**
   * Trả về đối tượng người dùng hiện đang được xác thực từ cơ sở dữ liệu hoặc null nếu không tìm thấy
   * hoặc chưa đăng nhập.
   *
   * @param userRepository kho lưu trữ dùng để truy vấn dữ liệu người dùng
   * @return đối tượng User hoặc null nếu không xác thực hoặc không tìm thấy
   */
  public static User getCurrentUser(
      UserRepository userRepository) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }

    Object principal = authentication.getPrincipal();

    if (principal instanceof UserDetails) {
      String username = ((UserDetails) principal).getUsername();
      return userRepository.findByUsername(username)
          .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    if (principal instanceof User) {
      return (User) principal;
    }

    return null;
  }

  /**
   * Lấy ID của người dùng đã đăng nhập.
   *
   * @return chuỗi tên đăng nhập hoặc null nếu chưa xác thực
   */
  public static String getMyUserId() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }
}
