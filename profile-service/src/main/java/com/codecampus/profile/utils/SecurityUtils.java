package com.codecampus.profile.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtils
{
  public static String getMyUserId() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }
}
