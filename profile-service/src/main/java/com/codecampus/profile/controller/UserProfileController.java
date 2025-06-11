package com.codecampus.profile.controller;

import com.codecampus.profile.dto.api.ApiResponse;
import com.codecampus.profile.dto.request.UserProfileUpdateRequest;
import com.codecampus.profile.dto.response.UserProfileResponse;
import com.codecampus.profile.service.UserProfileService;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class UserProfileController
{
  UserProfileService userProfileService;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/user/{profileId}")
  ApiResponse<UserProfileResponse> getUserProfileById(
      @PathVariable("profileId") String profileId) {
    return ApiResponse.<UserProfileResponse>builder()
        .result(userProfileService.getUserProfileById(profileId))
        .build();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/users")
  ApiResponse<List<UserProfileResponse>> getAllUserProfiles() {
    return ApiResponse.<List<UserProfileResponse>>builder()
        .result(userProfileService.getAllUserProfiles())
        .build();
  }

  @GetMapping("/user/my-profile")
  ApiResponse<UserProfileResponse> getMyUserProfile() {
    return ApiResponse.<UserProfileResponse>builder()
        .result(userProfileService.getMyUserProfile())
        .build();
  }

  @PatchMapping("/user/my-profile")
  ApiResponse<UserProfileResponse> updateMyUserProfile(
      @RequestBody UserProfileUpdateRequest request) {
    return ApiResponse.<UserProfileResponse>builder()
        .result(userProfileService.updateMyUserProfile(request))
        .build();
  }
}
