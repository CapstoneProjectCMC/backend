package com.codecampus.quiz.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;

/**
 * Tiện ích hỗ trợ lấy thông tin người dùng hiện đang đăng nhập
 * từ ngữ cảnh bảo mật của Spring Security.
 */
@Slf4j
public class AuthenticationHelper {
    /**
     * Lấy ID của người dùng đã đăng nhập.
     *
     * @return chuỗi tên đăng nhập hoặc null nếu chưa xác thực
     */
    public static String getMyUserId() {
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
