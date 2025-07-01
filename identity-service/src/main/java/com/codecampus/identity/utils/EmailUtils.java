package com.codecampus.identity.utils;

import org.springframework.util.StringUtils;

/**
 * Utility class for email-related operations.
 */
public final class EmailUtils
{
  // Không cho phép khởi tạo lớp
  private EmailUtils()
  {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  /**
   * Trích xuất username từ địa chỉ email (phần trước dấu '@').
   *
   * @param email địa chỉ email, ví dụ: "nguyen.van.a@example.com"
   * @return phần username, ví dụ: "nguyen.van.a"
   * @throws IllegalArgumentException nếu email null, rỗng, hoặc không chứa '@'
   */
  public static String extractUsername(String email)
  {
    if (!StringUtils.hasText(email))
    {
      throw new IllegalArgumentException("Email must not be null or empty");
    }

    int atIndex = email.indexOf('@');
    if (atIndex <= 0)
    {
      throw new IllegalArgumentException("Invalid email format: '" + email + "'");
    }

    return email.substring(0, atIndex);
  }
}
