package com.codecampus.identity.controller;

import com.codecampus.identity.dto.api.ApiResponse;
import com.codecampus.identity.dto.request.authentication.LoginRequest;
import com.codecampus.identity.dto.request.authentication.RefreshTokenRequest;
import com.codecampus.identity.dto.request.authentication.RegisterRequest;
import com.codecampus.identity.dto.response.authentication.LoginResponse;
import com.codecampus.identity.service.authentication.AuthenticationService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping("/auth")
public class AuthController
{
  AuthenticationService authenticationService;

  @PostMapping("/register")
  ApiResponse<Void> register(
      @RequestBody RegisterRequest request)
  {
    authenticationService.register(request);
    return ApiResponse.<Void>builder()
        .message("Register successfully!")
        .build();
  }

  @PostMapping("/login")
  ApiResponse<LoginResponse> login(
      @RequestBody LoginRequest request)
  {
    return ApiResponse.<LoginResponse>builder()
        .message("Login successfully!")
        .result(authenticationService.login(request))
        .build();
  }

  @PostMapping("/logout")
  ApiResponse<Void> logout()
  {
    authenticationService.logout();
    return ApiResponse.<Void>builder()
        .message("Logout successfully!")
        .build();
  }

  @PostMapping("/refresh-token")
  ApiResponse<LoginResponse> refreshToken(
      @RequestBody RefreshTokenRequest request)
  {
    return ApiResponse.<LoginResponse>builder()
        .message("Refresh token successfully!")
        .result(authenticationService.refreshToken(request))
        .build();
  }
}
