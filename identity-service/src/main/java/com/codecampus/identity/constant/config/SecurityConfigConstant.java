package com.codecampus.identity.constant.config;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SecurityConfigConstant {

  // PUBLIC ENDPOINT
  public static final String[] PUBLIC_ENDPOINTS = {
      "/health",
      "/actuator/**",
      "/auth/**",
  };

  // FRONTEND ENDPOINT (CORS)
  public static final String FRONTEND_ENDPOINT = "http://192.168.1.30:4200";
  public static final String FRONTEND_ENDPOINT2 = "http://localhost:4200";
  public static final String FRONTEND_ENDPOINT3 = "http://72.60.41.133:4200";

  // PATTERN REQUEST
  public static final String URL_PATTERN_ALL = "/**";

  // HEADER REQUEST
  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String CONTENT_TYPE_HEADER = "Content-Type";
  public static final String ACCEPT_HEADER = "Accept";

  // METHOD ALLOWED
  public static String GET_METHOD = "GET";
  public static String POST_METHOD = "POST";
  public static String DELETE_METHOD = "DELETE";
  public static String PUT_METHOD = "PUT";
  public static String PATCH_METHOD = "PATCH";
  public static String OPTIONS_METHOD = "OPTIONS";
}
