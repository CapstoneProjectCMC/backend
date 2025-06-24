package com.codecampus.submission.utils;

import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Tiện ích hỗ trợ lấy thông tin người dùng hiện đang đăng nhập
 * từ ngữ cảnh bảo mật của Spring Security.
 */
@Slf4j
public class SecurityUtils {
  /**
   * Lấy ID của người dùng đã đăng nhập.
   *
   * @return chuỗi tên đăng nhập hoặc null nếu chưa xác thực
   */
  public static String getMyUserId() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  public static Collection<String> getMyAuthorities() {
    Authentication auth =
        SecurityContextHolder.getContext().getAuthentication();
    return auth.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .toList();
  }

  public static Collection<String> getMyRoles() {
    return getMyAuthorities().stream()
        .filter(role -> role.startsWith("ROLE_"))
        .map(role -> role.substring("ROLE_".length()))
        .toList();
  }

  public static Collection<String> getPermissions() {
    return getMyAuthorities().stream()
        .filter(role -> !role.startsWith("ROLE_"))
        .toList();
  }
}
