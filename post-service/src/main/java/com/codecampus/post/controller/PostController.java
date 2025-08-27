package com.codecampus.post.controller;

import com.codecampus.post.dto.common.ApiResponse;
import com.codecampus.post.dto.common.PageResponse;
import com.codecampus.post.dto.request.PostRequestDto;
import com.codecampus.post.dto.response.PostResponseDto;
import com.codecampus.post.service.PostService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class PostController {

  PostService postService;

  @GetMapping("/my")
  ApiResponse<PageResponse<PostResponseDto>> getAllMyPosts(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {

    return ApiResponse.<PageResponse<PostResponseDto>>builder()
        .message("Get thành công các post của chính mình!")
        .result(postService.getAllMyPosts(page, size))
        .build();
  }

  @GetMapping("/view")
  ApiResponse<PageResponse<PostResponseDto>> getVisiblePosts(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {

    return ApiResponse.<PageResponse<PostResponseDto>>builder()
        .message("Get thành công các post có quyền xem!")
        .result(postService.getVisiblePosts(page, size))
        .build();
  }

  @GetMapping("/search")
  ApiResponse<PageResponse<PostResponseDto>> searchVisiblePosts(
      @RequestParam("q") String q,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<PostResponseDto>>builder()
        .message("Search thành công các post có quyền xem!")
        .result(postService.searchVisiblePosts(q, page, size))
        .build();
  }

  @GetMapping("/{postId}")
  ApiResponse<PostResponseDto> getPostDetail(
      @PathVariable("postId") String postId) {

    return ApiResponse.<PostResponseDto>builder()
        .message("Get thành công chi tiết post có quyền xem!")
        .result(postService.getPostDetail(postId))
        .build();
  }

  @PostMapping("/add")
  ApiResponse<Void> createPost(
      @ModelAttribute PostRequestDto postRequestDto) {
    postService.createPost(postRequestDto);
    return ApiResponse.<Void>builder()
        .message("Tạo post thành công!")
        .build();
  }

  @PatchMapping("/{postId}")
  ApiResponse<Void> updatePost(
      @PathVariable("postId") String postId,
      @ModelAttribute PostRequestDto postRequestDto) {
    postService.updatePost(postId, postRequestDto);
    return ApiResponse.<Void>builder()
        .message("Sửa post thành công!")
        .build();
  }

  @DeleteMapping("/{postId}")
  ApiResponse<Void> deletePost(
      @PathVariable("postId") String postId) {
    postService.deletePost(postId);
    return ApiResponse.<Void>builder()
        .message("Xoá thành công bài post!")
        .build();
  }
}
