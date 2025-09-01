package com.codecampus.organization.controller;

import com.codecampus.constant.ScopeType;
import com.codecampus.organization.dto.common.ApiResponse;
import com.codecampus.organization.dto.common.PageResponse;
import com.codecampus.organization.dto.request.BulkAddMembersRequest;
import com.codecampus.organization.dto.request.CreateOrganizationMemberRequest;
import com.codecampus.organization.dto.request.SwitchBlockRequest;
import com.codecampus.organization.dto.response.BlocksOfUserWithMemberResponse;
import com.codecampus.organization.dto.response.ImportMembersResult;
import com.codecampus.organization.dto.response.MemberInBlockWithMemberResponse;
import com.codecampus.organization.dto.response.PrimaryOrgResponse;
import com.codecampus.organization.service.MembershipService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MembershipController {

  MembershipService membershipService;

  @PostMapping("/{orgId}/member")
  ApiResponse<Void> addToOrg(
      @PathVariable String orgId,
      @RequestBody CreateOrganizationMemberRequest request) {
    membershipService.addToOrg(
        request.getUserId(),
        orgId,
        request.getRole(),
        request.isActive()
    );
    return ApiResponse.<Void>builder()
        .message("Thêm member vào tổ chức thành công!")
        .build();
  }

  @DeleteMapping("/{orgId}/member/{userId}")
  ApiResponse<Void> removeFromOrg(
      @PathVariable String orgId,
      @PathVariable String userId) {
    membershipService.removeMembership(
        userId,
        ScopeType.Organization,
        orgId);
    return ApiResponse.<Void>builder()
        .message("Xoá member khỏi tổ chức thành công!")
        .build();
  }

  @GetMapping("/{orgId}/members")
  ApiResponse<PageResponse<MemberInBlockWithMemberResponse>> listOrgMembers(
      @PathVariable String orgId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "true") boolean activeOnly) {
    return ApiResponse.<PageResponse<MemberInBlockWithMemberResponse>>builder()
        .message("Get toàn bộ member trong tổ chức!")
        .result(membershipService.listOrgMembers(orgId, page, size,
            activeOnly))
        .build();
  }

  @PostMapping("/block/{blockId}/member")
  ApiResponse<Void> addToBlock(
      @PathVariable String blockId,
      @RequestBody CreateOrganizationMemberRequest request) {
    membershipService.addToBlock(
        request.getUserId(),
        blockId,
        request.getRole(),
        request.isActive());
    return ApiResponse.<Void>builder()
        .message("Thêm member vào block thành công!")
        .build();
  }

  @DeleteMapping("/block/{blockId}/member/{userId}")
  ApiResponse<Void> removeMemberFromBlock(
      @PathVariable String blockId, @PathVariable String userId) {
    membershipService.removeMembership(
        userId,
        ScopeType.Grade,
        blockId
    );
    return ApiResponse.<Void>builder()
        .message("Xoá member khỏi block thành công!")
        .build();
  }

  @PatchMapping("/{orgId}/member/{userId}/switch-block")
  ApiResponse<Void> switchBlock(
      @PathVariable String orgId,
      @PathVariable String userId,
      @RequestBody SwitchBlockRequest request) {
    membershipService.switchBlock(
        userId,
        request.getFromBlockId(),
        request.getToBlockId(),
        request.getRole());
    return ApiResponse.<Void>builder()
        .message("Chuyển khối trong tổ chức thành công!")
        .build();
  }

  @DeleteMapping("/member/organization/{orgId}")
  ApiResponse<Void> leaveOrg(
      @PathVariable String orgId) {
    membershipService.leaveOrg(orgId);
    return ApiResponse.<Void>builder()
        .message("Rời tổ chức thành công!")
        .build();
  }

  @DeleteMapping("/member/block/{blockId}")
  ApiResponse<Void> leaveBlock(
      @PathVariable String blockId) {
    membershipService.leaveBlock(blockId);
    return ApiResponse.<Void>builder()
        .message("Rời block thành công!")
        .build();
  }

  @GetMapping("/member/{memberId}/primary-org")
  ApiResponse<PrimaryOrgResponse> getPrimaryOrg(
      @PathVariable("memberId") String memberId) {
    return ApiResponse.<PrimaryOrgResponse>builder()
        .message("Get thành công các tổ chức của user!")
        .result(membershipService.getPrimaryOrg(memberId))
        .build();
  }

  @PostMapping("/{orgId}/members:bulk")
  ApiResponse<Void> bulkAddToOrg(
      @PathVariable String orgId,
      @RequestBody BulkAddMembersRequest request) {
    membershipService.bulkAddToOrg(orgId, request);
    return ApiResponse.<Void>builder()
        .message("Bulk add members vào tổ chức thành công!")
        .build();
  }

  @PostMapping("/block/{blockId}/members:bulk")
  ApiResponse<Void> bulkAddToBlock(
      @PathVariable String blockId,
      @RequestBody BulkAddMembersRequest request) {
    membershipService.bulkAddToBlock(blockId, request);
    return ApiResponse.<Void>builder()
        .message("Bulk add members vào block thành công!").build();
  }

  @PostMapping(
      value = "/{orgId}/members:import",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  ApiResponse<ImportMembersResult> importMembersToOrg(
      @PathVariable String orgId,
      @RequestPart("file") MultipartFile file) {
    return ApiResponse.<ImportMembersResult>builder()
        .message("Import members vào tổ chức thành công!")
        .result(membershipService.importMembersToOrg(orgId, file))
        .build();
  }

  @PostMapping(
      value = "/block/{blockId}/members:import",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  ApiResponse<ImportMembersResult> importMembersToBlock(
      @PathVariable String blockId,
      @RequestPart("file") MultipartFile file) {
    return ApiResponse.<ImportMembersResult>builder()
        .message("Import members vào block thành công!")
        .result(membershipService.importMembersToBlock(blockId, file))
        .build();
  }

  /**
   * Dành cho identity lấy block_ids user đang tham gia để put vào JWT
   */
  @GetMapping("/member/{userId}/blocks")
  ApiResponse<BlocksOfUserWithMemberResponse> listActiveBlocksOfUser(
      @PathVariable String userId) {
    return ApiResponse.<BlocksOfUserWithMemberResponse>builder()
        .message("Get các block đang active của user thành công!")
        .result(membershipService.listActiveBlocksOfUser(userId))
        .build();
  }
}
