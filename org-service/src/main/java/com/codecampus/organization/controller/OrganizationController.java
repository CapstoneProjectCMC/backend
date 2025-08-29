package com.codecampus.organization.controller;

import com.codecampus.organization.dto.common.ApiResponse;
import com.codecampus.organization.dto.common.PageResponse;
import com.codecampus.organization.dto.request.BlockWithMembersPageResponse;
import com.codecampus.organization.dto.request.CreateOrganizationForm;
import com.codecampus.organization.dto.request.UpdateOrganizationForm;
import com.codecampus.organization.dto.response.MemberInBlockResponse;
import com.codecampus.organization.dto.response.OrganizationResponse;
import com.codecampus.organization.service.BlockService;
import com.codecampus.organization.service.MembershipService;
import com.codecampus.organization.service.OrganizationService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationController {
  OrganizationService organizationService;
  BlockService blockService;
  MembershipService membershipService;

  @PostMapping(
      value = "/organization",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ApiResponse<Void> create(
      @ModelAttribute CreateOrganizationForm request) {
    organizationService.create(request);
    return ApiResponse.<Void>builder()
        .message("Thêm tổ chức thành công!")
        .build();
  }

  @PatchMapping(value = "/organization/{orgId}",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ApiResponse<Void> update(
      @PathVariable("orgId") String orgId,
      @ModelAttribute UpdateOrganizationForm request) {
    organizationService.update(orgId, request);
    return ApiResponse.<Void>builder()
        .message("Sửa tổ chức thành công!")
        .build();
  }

  @DeleteMapping("/organization/{orgId}")
  ApiResponse<Void> delete(
      @PathVariable("orgId") String orgId) {
    organizationService.delete(orgId);
    return ApiResponse.<Void>builder()
        .message("Xoá tổ chức thành công!")
        .build();
  }

  @GetMapping("/organizations")
  ApiResponse<PageResponse<OrganizationResponse>> list(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<OrganizationResponse>>builder()
        .message("Get các tổ chức thành công!")
        .result(organizationService.list(page, size))
        .build();
  }

  @GetMapping("/organization/{orgId}")
  ApiResponse<OrganizationResponse> get(
      @PathVariable("orgId") String orgId) {
    return ApiResponse.<OrganizationResponse>builder()
        .message("Get thông tin tổ chức thành công!")
        .result(organizationService.get(orgId))
        .build();
  }

  @GetMapping("/{orgId}/blocks")
  ApiResponse<PageResponse<BlockWithMembersPageResponse>> getBlocksOfOrg(
      @PathVariable String orgId,
      @RequestParam(defaultValue = "1") int blocksPage,
      @RequestParam(defaultValue = "10") int blocksSize,
      @RequestParam(defaultValue = "1") int membersPage,
      @RequestParam(defaultValue = "10") int membersSize,
      @RequestParam(defaultValue = "true") boolean activeOnlyMembers,
      @RequestParam(defaultValue = "true") boolean includeUnassigned) {
    return ApiResponse.<PageResponse<BlockWithMembersPageResponse>>builder()
        .message("Get thông tin các khối trong tổ chức thành công!")
        .result(blockService.getBlocksOfOrg(orgId, blocksPage, blocksSize,
            membersPage, membersSize, activeOnlyMembers, includeUnassigned))
        .build();
  }

  @GetMapping("/{orgId}/members/unassigned")
  ApiResponse<PageResponse<MemberInBlockResponse>> listUnassignedMembers(
      @PathVariable String orgId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "true") boolean activeOnly) {
    return ApiResponse.<PageResponse<MemberInBlockResponse>>builder()
        .message("Get members thuộc tổ chức nhưng chưa ở block nào!")
        .result(membershipService.listUnassignedMembers(orgId, page, size,
            activeOnly))
        .build();
  }
}
