package com.codecampus.profile.controller;

import com.codecampus.profile.dto.common.ApiResponse;
import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.dto.request.ReportRequest;
import com.codecampus.profile.entity.properties.post.Reaction;
import com.codecampus.profile.entity.properties.post.ReportedPost;
import com.codecampus.profile.entity.properties.post.SavedPost;
import com.codecampus.profile.service.PostService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class PostController {
  PostService postService;

  @PostMapping("/post/{postId}/save")
  ApiResponse<Void> savePost(
      @PathVariable String postId) {
    postService.savePost(postId);
    return ApiResponse.<Void>builder()
        .message("Đã lưu bài viết")
        .build();
  }

  @DeleteMapping("/post/{postId}/save")
  ApiResponse<Void> unsavePost(
      @PathVariable String postId) {
    postService.unsavePost(postId);
    return ApiResponse.<Void>builder()
        .message("Đã bỏ lưu bài viết")
        .build();
  }

  @PostMapping("/post/{postId}/report")
  ApiResponse<Void> reportPost(@PathVariable String postId,
                               @RequestBody ReportRequest body) {
    postService.reportPost(postId, body.reason());
    return ApiResponse.<Void>builder()
        .message("Đã báo cáo bài viết")
        .build();
  }

  @GetMapping("/posts/saved")
  ApiResponse<PageResponse<SavedPost>> getSavedPosts(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<SavedPost>>builder()
        .message("Bài viết đã lưu")
        .result(postService.getSavedPosts(page, size))
        .build();
  }

  @GetMapping("/post/reactions")
  ApiResponse<PageResponse<Reaction>> getMyReactions(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<Reaction>>builder()
        .message("Phản ứng của tôi")
        .result(postService.getMyReactions(page, size))
        .build();
  }

  @GetMapping("/posts/reported")
  ApiResponse<PageResponse<ReportedPost>> reportedPosts(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<ReportedPost>>builder()
        .message("Bài viết tôi đã báo cáo")
        .result(postService.getReportedPosts(page, size))
        .build();
  }
}
