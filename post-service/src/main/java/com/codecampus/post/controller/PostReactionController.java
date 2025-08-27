package com.codecampus.post.controller;

import com.codecampus.post.dto.common.ApiResponse;
import com.codecampus.post.dto.request.PostReactionToggleRequestDto;
import com.codecampus.post.service.PostReactionService;
import java.util.Map;
import lombok.AccessLevel;
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
@Slf4j
public class PostReactionController {

  PostReactionService postReactionService;

  // Toggle reaction (upvote / downvote)
  @PostMapping("/{postId}/reaction/toggle")
  ApiResponse<Void> togglePostReaction(
      @PathVariable String postId,
      @RequestBody PostReactionToggleRequestDto dto) {
    postReactionService.togglePostReaction(
        postId, dto.getReactionType());
    return ApiResponse.<Void>builder()
        .message("Reaction toggled thành công!")
        .build();
  }

  @PostMapping("/{postId}/comment/{commentId}/reaction/toggle")
  ApiResponse<Void> toggleCommentReaction(
      @PathVariable String postId,
      @PathVariable String commentId,
      @RequestBody PostReactionToggleRequestDto dto) {
    postReactionService.toggleCommentReaction(
        postId,
        commentId,
        dto.getReactionType());
    return ApiResponse.<Void>builder()
        .message("Reaction (comment) toggled!")
        .build();
  }

  // Get reaction count cho post
  @GetMapping("/{postId}/reactions/count")
  ApiResponse<Map<String, Long>> postCounts(
      @PathVariable String postId) {
    return ApiResponse.<Map<String, Long>>builder()
        .message("Tính thành công số reaction post!")
        .result(postReactionService.getReactionCount(postId, null))
        .build();
  }

  // Get reaction count cho comment trong post
  @GetMapping("/{postId}/comments/{commentId}/reactions/count")
  ApiResponse<Map<String, Long>> commentCounts(
      @PathVariable String postId,
      @PathVariable String commentId) {
    return ApiResponse.<Map<String, Long>>builder()
        .message("Tính thành công số reaction comment!")
        .result(postReactionService.getReactionCount(postId, commentId))
        .build();
  }
}
