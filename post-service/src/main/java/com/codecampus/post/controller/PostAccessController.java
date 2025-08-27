package com.codecampus.post.controller;

import com.codecampus.post.dto.common.ApiResponse;
import com.codecampus.post.dto.common.PageResponse;
import com.codecampus.post.dto.request.PostAccessBulkDeleteRequestDto;
import com.codecampus.post.dto.request.PostAccessUpsertRequestDto;
import com.codecampus.post.dto.response.PostAccessResponseDto;
import com.codecampus.post.service.PostAccessService;
import lombok.AccessLevel;
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
@Slf4j
public class PostAccessController {

  private final PostAccessService postAccessService;

  @PostMapping("/{postId}/access")
  ApiResponse<Void> upsertAccess(
      @PathVariable String postId,
      @RequestBody PostAccessUpsertRequestDto dto) {
    postAccessService.upsertAccess(
        postId, dto);
    return ApiResponse.<Void>builder()
        .message("Cập nhật danh sách access thành công!")
        .build();
  }

  @GetMapping("/{postId}/access")
  ApiResponse<PageResponse<PostAccessResponseDto>> getAccessByPostId(
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

  @DeleteMapping("/access/{accessId}")
  ApiResponse<Void> softDeleteAccess(
      @PathVariable String accessId) {
    postAccessService.softDeleteAccess(
        accessId);
    return ApiResponse.<Void>builder()
        .message("Xóa access thành công!")
        .build();
  }

  @DeleteMapping("/{postId}/accesses")
  ApiResponse<Void> softDeleteAccessByUsers(
      @PathVariable String postId,
      @RequestBody PostAccessBulkDeleteRequestDto dto) {
    postAccessService.softDeleteAccessByUsers(
        postId, dto.getUserIds());
    return ApiResponse.<Void>builder()
        .message("Xoá access theo user thành công!")
        .build();
  }
}
