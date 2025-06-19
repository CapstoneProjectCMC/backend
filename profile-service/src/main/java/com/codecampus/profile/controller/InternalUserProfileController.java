package com.codecampus.profile.controller;

import com.codecampus.profile.dto.common.ApiResponse;
import com.codecampus.profile.dto.request.UserProfileCreationRequest;
import com.codecampus.profile.dto.response.UserProfileResponse;
import com.codecampus.profile.service.UserProfileService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class InternalUserProfileController {
  UserProfileService userProfileService;

  @PostMapping("/internal/user")
  ApiResponse<UserProfileResponse> createUserProfile(
      @RequestBody UserProfileCreationRequest request) {
    return ApiResponse.<UserProfileResponse>builder()
        .result(userProfileService.createUserProfile(request))
        .build();
  }

  @GetMapping("/internal/user/{userId}")
  ApiResponse<UserProfileResponse> getUserProfileByUserId(
      @PathVariable("userId") String userId) {
    return ApiResponse.<UserProfileResponse>builder()
        .result(userProfileService.getUserProfileByUserId(userId))
        .build();
  }
}
