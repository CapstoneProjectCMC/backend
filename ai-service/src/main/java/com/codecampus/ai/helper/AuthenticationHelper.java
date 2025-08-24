package com.codecampus.ai.helper;

import java.util.Collection;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Tiện ích hỗ trợ lấy thông tin người dùng hiện đang đăng nhập
 * từ ngữ cảnh bảo mật của Spring Security.
 */
@Slf4j
@UtilityClass
public class AuthenticationHelper {
  public Collection<String> getMyRoles() {
    return getMyAuthorities().stream()
        .filter(role -> role.startsWith("ROLE_"))
        .map(role -> role.substring("ROLE_".length()))
        .toList();
  }

  public Collection<String> getPermissions() {
    return getMyAuthorities().stream()
        .filter(role -> !role.startsWith("ROLE_"))
        .toList();
  }

  /**
   * Lấy ID của người dùng đã đăng nhập.
   *
   * @return chuỗi tên đăng nhập hoặc null nếu chưa xác thực
   */
  public String getMyUserId() {
    Authentication auth =
        SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      return null;
    }

    Object principal = auth.getPrincipal();

    if (principal instanceof Jwt jwt) {
      // JwtAuthenticationToken giữ nguyên đối tượng Jwt làm principal,
      return jwt.getClaimAsString("userId");
    }

    return null;
  }

  public Collection<String> getMyAuthorities() {
    Authentication auth =
        SecurityContextHolder.getContext().getAuthentication();
    return auth.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .toList();
  }
}
