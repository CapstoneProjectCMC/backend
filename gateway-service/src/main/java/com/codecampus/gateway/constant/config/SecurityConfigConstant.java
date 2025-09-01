package com.codecampus.gateway.constant.config;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SecurityConfigConstant {

  // PATTERN REQUEST
  public static final String URL_PATTERN_ALL = "/**";

  // PUBLIC ENDPOINTS FOR AUTHENTICATION FILTER
  public static final String[] PUBLIC_ENDPOINTS = {
      "/identity/auth/.*",
      "/ai/files/.*",
      "/search/.*",
      "/org/member/.*"
  };
}
