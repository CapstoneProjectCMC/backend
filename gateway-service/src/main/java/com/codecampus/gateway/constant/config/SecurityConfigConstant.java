package com.codecampus.gateway.constant.config;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SecurityConfigConstant
{

  // PATTERN REQUEST
  public static final String URL_PATTERN_ALL = "/**";

  // PUBLIC ENDPOINTS FOR AUTHENTICATION FILTER
  public static final String[] PUBLIC_ENDPOINTS = {
      "/identity/hello",
      "/identity/auth/.*",
      "/profile/hello",
      "/file/hello",
      "/submission/hello",
  };

  // CORS IDENTITY SERVICE ENDPOINT
  public static final String IDENTITY_SERVICE_ENDPOINT =
      "https://localhost:8080/identity";
}
