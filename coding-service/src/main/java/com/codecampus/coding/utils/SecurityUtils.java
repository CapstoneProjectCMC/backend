package com.codecampus.coding.utils;

import lombok.extern.slf4j.Slf4j;
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
}
