package com.codecampus.post.controller;

import com.codecampus.post.dto.common.ApiResponse;
import com.codecampus.post.dto.common.PageResponse;
import com.codecampus.post.dto.response.PostAccessResponseDto;
import com.codecampus.post.service.PostAccessService;
import com.codecampus.post.service.PostCommentService;
import com.codecampus.post.service.PostReactionService;
import com.codecampus.post.service.PostService;
import dtos.PostSummary;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequestMapping("/internal")
public class InternalPostController {

  PostService postService;
  PostAccessService postAccessService;
  PostReactionService postReactionService;
  PostCommentService postCommentService;

  @GetMapping("/post/{postId}/summary")
  public ApiResponse<PostSummary> internalGetPostSummary(
      @PathVariable String postId) {
    return ApiResponse.<PostSummary>builder()
        .result(postService.getPostSummary(postId))
        .message("Get Post summary thành công!")
        .build();
  }

  @GetMapping("/{postId}/access")
  ApiResponse<PageResponse<PostAccessResponseDto>> internalGetAccessByPostId(
      @PathVariable String postId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<PostAccessResponseDto>>builder()
        .message("Lấy danh sách access thành công!")
        .result(postAccessService.getAccessByPostId(
            postId,
            page, size))
        .build();
  }

  // Get reaction count cho post
  @GetMapping("/{postId}/reactions/count")
  ApiResponse<Map<String, Long>> internalPostCounts(
      @PathVariable String postId) {
    return ApiResponse.<Map<String, Long>>builder()
        .message("Tính thành công số reaction post!")
        .result(postReactionService.getReactionCount(postId, null))
        .build();
  }

  // Get reaction count cho comment trong post
  @GetMapping("/{postId}/comments/count")
  ApiResponse<Long> internalCommentCount(@PathVariable String postId) {
    long c = postCommentService.countByPost(postId);
    return ApiResponse.<Long>builder()
        .message("Đếm bình luận thành công!")
        .result(c)
        .build();
  }
}
