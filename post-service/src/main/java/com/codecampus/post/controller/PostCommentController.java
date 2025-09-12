package com.codecampus.post.controller;

import com.codecampus.post.dto.common.ApiResponse;
import com.codecampus.post.dto.common.PageResponse;
import com.codecampus.post.dto.request.CommentCreateRequestDto;
import com.codecampus.post.dto.request.UpdateCommentDto;
import com.codecampus.post.dto.response.CommentCreatedDto;
import com.codecampus.post.dto.response.CommentResponseDto;
import com.codecampus.post.service.PostCommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostCommentController {

  PostCommentService postCommentService;

  @PostMapping("/{postId}/comment")
  ApiResponse<CommentCreatedDto> addComment(
      @PathVariable String postId,
      @RequestBody CommentCreateRequestDto dto) {
    return ApiResponse.<CommentCreatedDto>builder()
        .message("Thêm bình luận thành công!")
        .result(postCommentService.addTopLevelComment(
            postId, dto.getContent()))
        .build();
  }

  @PostMapping("/{postId}/comment/{parentCommentId}")
  ApiResponse<CommentCreatedDto> addReply(
      @PathVariable String postId,
      @PathVariable String parentCommentId,
      @RequestBody CommentCreateRequestDto dto) {
    return ApiResponse.<CommentCreatedDto>builder()
        .message("Thêm trả lời thành công!")
        .result(postCommentService.addReply(
            postId, parentCommentId,
            dto.getContent()))
        .build();
  }

  @GetMapping("/{postId}/comments")
  ApiResponse<PageResponse<CommentResponseDto>> getCommentsByPost(
      @PathVariable String postId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "3") int replySize) {
    return ApiResponse.<PageResponse<CommentResponseDto>>builder()
        .message("Lấy bình luận thành công!")
        .result(
            postCommentService.getCommentsByPost(
                postId, page, size, replySize))
        .build();
  }

  @PatchMapping("/comment/{commentId}")
  ApiResponse<Void> updateComment(
      @PathVariable String commentId,
      @RequestBody UpdateCommentDto dto) {
    postCommentService.updateComment(
        commentId, dto.getContent());
    return ApiResponse.<Void>builder()
        .message("Bình luận chỉnh sửa thành công!")
        .build();
  }

  @DeleteMapping("/comment/{commentId}")
  ApiResponse<Void> deleteComment(
      @PathVariable String commentId) {
    postCommentService.deleteComment(commentId);
    return ApiResponse.<Void>builder()
        .message("Xoá bình luận thành công!")
        .build();
  }

  @GetMapping("/{postId}/comments/count")
  ApiResponse<Long> countComments(@PathVariable String postId) {
    long c = postCommentService.countByPost(postId);
    return ApiResponse.<Long>builder()
        .message("Đếm bình luận thành công!")
        .result(c)
        .build();
  }
}

