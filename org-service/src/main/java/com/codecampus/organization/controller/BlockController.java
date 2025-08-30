package com.codecampus.organization.controller;

import com.codecampus.organization.dto.common.ApiResponse;
import com.codecampus.organization.dto.request.BlockWithMembersResponse;
import com.codecampus.organization.dto.request.CreateBlockRequest;
import com.codecampus.organization.dto.request.UpdateBlockRequest;
import com.codecampus.organization.service.BlockService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlockController {
  BlockService blockService;

  @PostMapping("/{orgId}/block")
  public ApiResponse<Void> createBlock(
      @PathVariable String orgId,
      @RequestBody CreateBlockRequest request) {
    blockService.createBlock(orgId, request);
    return ApiResponse.<Void>builder()
        .message("Tạo khối thành công!")
        .build();
  }

  @PatchMapping("/block/{blockId}")
  public ApiResponse<Void> updateBlock(
      @PathVariable String blockId,
      @RequestBody UpdateBlockRequest request) {
    blockService.updateBlock(blockId, request);
    return ApiResponse.<Void>builder()
        .message("Sửa khối thành công!")
        .build();
  }

  @DeleteMapping("/block/{blockId}")
  public ApiResponse<Void> deleteBlock(
      @PathVariable String blockId) {
    blockService.deleteBlock(blockId);
    return ApiResponse.<Void>builder()
        .message("Xoá khối thành công!")
        .build();
  }

  @GetMapping("/block/{blockId}")
  public ApiResponse<BlockWithMembersResponse> getBlock(
      @PathVariable String blockId,
      @RequestParam(defaultValue = "1") int membersPage,
      @RequestParam(defaultValue = "10") int membersSize,
      @RequestParam(defaultValue = "true") boolean activeOnly) {
    return ApiResponse.<BlockWithMembersResponse>builder()
        .message("Get các thông tin của khối thành công!")
        .result(blockService.getBlock(
            blockId,
            membersPage,
            membersSize,
            activeOnly))
        .build();
  }
}
