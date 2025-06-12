package com.codecampus.identity.controller.authentication;

import com.codecampus.identity.dto.common.ApiResponse;
import com.codecampus.identity.dto.request.authentication.AuthenticationRequest;
import com.codecampus.identity.dto.request.authentication.IntrospectRequest;
import com.codecampus.identity.dto.request.authentication.LogoutRequest;
import com.codecampus.identity.dto.request.authentication.RefreshRequest;
import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.response.authentication.AuthenticationResponse;
import com.codecampus.identity.dto.response.authentication.IntrospectResponse;
import com.codecampus.identity.service.authentication.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import java.text.ParseException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping("/auth")
public class AuthenticationController
{
  AuthenticationService authenticationService;

  @PostMapping("/login-google")
  ApiResponse<AuthenticationResponse> outboundGoogleLogin(
      @RequestParam("code") String code) throws ParseException
  {
    return ApiResponse.<AuthenticationResponse>builder()
        .result(authenticationService.outboundGoogleLogin(code))
        .build();
  }

  @PostMapping("/login")
  ApiResponse<AuthenticationResponse> login(
      @RequestBody AuthenticationRequest request)
      throws ParseException
  {
    return ApiResponse.<AuthenticationResponse>builder()
        .result(authenticationService.login(request))
        .message("Login successful")
        .build();
  }

  @PostMapping("/register")
  ApiResponse<Void> register(
      @RequestBody UserCreationRequest request) {
    authenticationService.register(request);
    return ApiResponse.<Void>builder()
        .message("Register successful. Check OTP Send to mail")
        .build();
  }

  @PostMapping("/introspect")
  ApiResponse<IntrospectResponse> introspect(
      @RequestBody IntrospectRequest request)
      throws ParseException, JOSEException
  {
    return ApiResponse.<IntrospectResponse>builder()
        .result(authenticationService.introspect(request))
        .message("Introspection successful")
        .build();
  }

  @PostMapping("/refresh")
  ApiResponse<AuthenticationResponse> refreshToken(
      @RequestBody RefreshRequest request)
      throws ParseException, JOSEException {
    return ApiResponse.<AuthenticationResponse>builder()
        .result(authenticationService.refreshToken(request))
        .message("Refresh successful")
        .build();
  }

  @PostMapping("/logout")
  ApiResponse<Void> logout(
      @RequestBody LogoutRequest request)
      throws ParseException, JOSEException {
    authenticationService.logout(request);
    return ApiResponse.<Void>builder()
        .message("Logout successful")
        .build();
  }
}
