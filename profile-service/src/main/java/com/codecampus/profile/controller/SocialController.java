package com.codecampus.profile.controller;

import com.codecampus.profile.dto.common.ApiResponse;
import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.entity.properties.social.Blocks;
import com.codecampus.profile.entity.properties.social.Follows;
import com.codecampus.profile.service.SocialService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping("/social")
public class SocialController {
  SocialService socialService;

  @PostMapping("/follow/{targetUserId}")
  ApiResponse<Void> follow(
      @PathVariable String targetUserId) {
    socialService.follow(targetUserId);
    return ApiResponse.<Void>builder()
        .message("Đã follow")
        .build();
  }

  @DeleteMapping("/follow/{targetUserId}")
  ApiResponse<Void> unfollow(
      @PathVariable String targetUserId) {
    socialService.unfollow(targetUserId);
    return ApiResponse.<Void>builder()
        .message("Đã unfollow")
        .build();
  }

  @PostMapping("/block/{targetUserId}")
  ApiResponse<Void> block(
      @PathVariable String targetUserId) {
    socialService.block(targetUserId);
    return ApiResponse.<Void>builder()
        .message("Đã block")
        .build();
  }

  @DeleteMapping("/block/{targetUserId}")
  ApiResponse<Void> unblock(
      @PathVariable String targetUserId) {
    socialService.unblock(targetUserId);
    return ApiResponse.<Void>builder()
        .message("Đã unblock")
        .build();
  }

  @GetMapping("/followers")
  ApiResponse<PageResponse<Follows>> getFollowers(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<Follows>>builder()
        .message("Followers")
        .result(socialService.getFollowers(page, size))
        .build();
  }

  @GetMapping("/followings")
  ApiResponse<PageResponse<Follows>> getFollowings(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<Follows>>builder()
        .message("Followings")
        .result(socialService.getFollowings(page, size))
        .build();
  }

  @GetMapping("/blocked")
  ApiResponse<PageResponse<Blocks>> findBlocked(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<Blocks>>builder()
        .message("Danh sách blocked")
        .result(socialService.findBlocked(page, size))
        .build();
  }
}
