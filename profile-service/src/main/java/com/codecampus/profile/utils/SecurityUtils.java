package com.codecampus.profile.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils
{
  public static String getMyUserId() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }
}
