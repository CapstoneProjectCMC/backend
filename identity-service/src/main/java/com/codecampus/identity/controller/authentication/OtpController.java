package com.codecampus.identity.controller.authentication;

import com.codecampus.identity.dto.common.ApiResponse;
import com.codecampus.identity.dto.request.authentication.OtpVerificationRequest;
import com.codecampus.identity.dto.request.authentication.UserCreationRequest;
import com.codecampus.identity.dto.response.authentication.OtpResponse;
import com.codecampus.identity.service.authentication.OtpService;
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
@RequestMapping("/auth")
public class OtpController {
  OtpService otpService;

  @PostMapping("/send-otp")
  public ApiResponse<OtpResponse> sendOtp(
      @RequestBody UserCreationRequest request) {
    return ApiResponse.<OtpResponse>builder()
        .result(otpService.sendOtp(request))
        .message("OTP sent successfully")
        .build();
  }

  @PostMapping("/verify-otp")
  public ApiResponse<Void> verifyOtp(
      @RequestBody OtpVerificationRequest request) {
    otpService.verifyOtp(request);
    return ApiResponse.<Void>builder()
        .message("OTP verified successfully")
        .build();
  }
}
