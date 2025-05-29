package com.codecampus.gateway.constant.config;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SecurityConfigConstant
{

  // PATTERN REQUEST
  public static final String URL_PATTERN_ALL = "/**";

  // CORS IDENTITY SERVICE ENDPOINT
  public static final String IDENTITY_SERVICE_ENDPOINT =
      "http://localhost:8080/identity";
}
