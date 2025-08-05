package com.codecampus.profile.controller;

import com.codecampus.profile.dto.common.ApiResponse;
import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.dto.request.UserProfileUpdateRequest;
import com.codecampus.profile.dto.response.UserProfileResponse;
import com.codecampus.profile.service.ProfileImageService;
import com.codecampus.profile.service.UserProfileService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class UserProfileController {
    UserProfileService userProfileService;
    ProfileImageService profileImageService;

    @GetMapping("/user/{profileId}")
    ApiResponse<UserProfileResponse> getUserProfileById(
            @PathVariable("profileId") String profileId) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getUserProfileById(profileId))
                .message("Get thành công profile!")
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    ApiResponse<PageResponse<UserProfileResponse>> getAllUserProfiles(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<UserProfileResponse>>builder()
                .result(userProfileService.getAllUserProfiles(page, size))
                .message("Get thành công tất cả các profile!")
                .build();
    }

    @GetMapping("/user/my-profile")
    ApiResponse<UserProfileResponse> getMyUserProfile() {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getMyUserProfile())
                .message("Get thành công profile!")
                .build();
    }

    @PatchMapping("/user/my-profile")
    ApiResponse<UserProfileResponse> updateMyUserProfile(
            @RequestBody UserProfileUpdateRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.updateMyUserProfile(request))
                .message("Update thành công profile!")
                .build();
    }

    @PostMapping(
            value = "/user/my-profile/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<Void> uploadAvatar(
            @RequestPart("file") MultipartFile file) {
        profileImageService.uploadAvatar(file);
        return ApiResponse.<Void>builder()
                .message("Upload thành công avatar!")
                .build();
    }

    @PostMapping(
            value = "/user/my-profile/background",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<Void> uploadBackground(
            @RequestPart("file") MultipartFile file) {
        profileImageService.uploadBackground(file);
        return ApiResponse.<Void>builder()
                .message("Upload thành công background!")
                .build();
    }

    @PatchMapping(
            value = "/user/my-profile/avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<Void> updateAvatar(
            @RequestPart("file") MultipartFile file) {
        profileImageService.updateAvatar(file);
        return ApiResponse.<Void>builder()
                .message("Đã cập nhật avatar!")
                .build();
    }

    @PatchMapping(value = "/user/my-profile/background",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiResponse<Void> updateBackground(
            @RequestPart("file") MultipartFile file) {
        profileImageService.updateBackground(file);
        return ApiResponse.<Void>builder()
                .message("Đã cập nhật background!")
                .build();
    }

}
