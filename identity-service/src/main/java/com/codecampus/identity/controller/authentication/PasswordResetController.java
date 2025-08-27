package com.codecampus.identity.controller.authentication;

import com.codecampus.identity.dto.common.ApiResponse;
import com.codecampus.identity.dto.request.authentication.ForgotPasswordRequest;
import com.codecampus.identity.dto.request.authentication.ResetPasswordRequest;
import com.codecampus.identity.dto.request.authentication.VerifyPasswordResetRequest;
import com.codecampus.identity.dto.response.authentication.OtpResponse;
import com.codecampus.identity.service.authentication.PasswordResetService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping("/auth/forgot-password")
public class PasswordResetController {

  PasswordResetService passwordResetService;

  @PostMapping("/request")
  ApiResponse<OtpResponse> request(
      @RequestBody ForgotPasswordRequest request) {
    return ApiResponse.<OtpResponse>builder()
        .message("Request lấy mật khẩu thành công!")
        .result(passwordResetService.requestReset(request))
        .build();
  }

  @PostMapping("/verify")
  ApiResponse<Void> verify(
      @RequestBody VerifyPasswordResetRequest request) {
    passwordResetService.verifyOtp(request);
    return ApiResponse.<Void>builder()
        .message("Verify otp thành công!")
        .build();
  }

  @PostMapping("/reset")
  ApiResponse<Void> reset(
      @RequestBody ResetPasswordRequest request) {
    passwordResetService.resetPassword(request);
    return ApiResponse.<Void>builder()
        .message("Reset password thành công!")
        .build();
  }
}
