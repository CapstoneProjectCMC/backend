package com.codecampus.profile.controller;

import com.codecampus.profile.dto.common.ApiResponse;
import com.codecampus.profile.dto.request.UserProfileCreationRequest;
import com.codecampus.profile.dto.request.UserProfileUpdateRequest;
import com.codecampus.profile.dto.response.UserProfileResponse;
import com.codecampus.profile.service.UserProfileService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/internal")
public class InternalUserProfileController {
    UserProfileService userProfileService;

    @PostMapping("/user")
    ApiResponse<UserProfileResponse> internalCreateUserProfile(
            @RequestBody UserProfileCreationRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.createUserProfile(request))
                .build();
    }

    @GetMapping("/user/{userId}")
    ApiResponse<UserProfileResponse> internalGetUserProfileByUserId(
            @PathVariable("userId") String userId) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getUserProfileByUserId(userId))
                .build();
    }

    @PatchMapping("/user/{userId}")
    ApiResponse<Void> internalUpdateProfileByUserId(
            @PathVariable String userId,
            @RequestBody UserProfileUpdateRequest request) {
        userProfileService.updateUserProfileById(userId, request);
        return ApiResponse.<Void>builder()
                .build();
    }

    @DeleteMapping("/user/{userId}")
    ApiResponse<Void> internalSoftDeleteByUserId(
            @PathVariable String userId,
            @RequestParam(required = false) String deletedBy) {
        userProfileService.softDeleteUserProfileByUserId(
                userId, deletedBy);
        return ApiResponse.<Void>builder()
                .build();
    }

    @PatchMapping("/user/{userId}/restore")
    ApiResponse<Void> restoreByUserId(
            @PathVariable String userId) {
        userProfileService.restoreByUserId(userId);
        return ApiResponse.<Void>builder()
                .build();
    }
}
