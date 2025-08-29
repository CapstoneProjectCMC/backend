package com.codecampus.organization.controller;

import com.codecampus.constant.ScopeType;
import com.codecampus.organization.dto.common.ApiResponse;
import com.codecampus.organization.dto.request.CreateOrganizationMemberRequest;
import com.codecampus.organization.dto.response.PrimaryOrgResponse;
import com.codecampus.organization.service.OrganizationMemberService;
import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationMemberController {

  OrganizationMemberService service;

  @PostMapping("/member")
  ApiResponse<Void> addMembership(
      @RequestBody CreateOrganizationMemberRequest request) {
    service.addMembership(request);
    return ApiResponse.<Void>builder()
        .message("Thêm thành viên thành công!")
        .build();
  }

  @PostMapping("/member/bulk")
  ApiResponse<Void> bulkAddMemberships(
      @RequestBody List<CreateOrganizationMemberRequest> requests) {
    service.bulkAddMemberships(requests);
    return ApiResponse.<Void>builder()
        .message("Thêm nhiều thành viên thành công!")
        .build();
  }

  @GetMapping("/member/{memberId}/primary-org")
  ApiResponse<PrimaryOrgResponse> getPrimaryOrg(
      @PathVariable("memberId") String memberId) {
    return ApiResponse.<PrimaryOrgResponse>builder()
        .message("Get thành công các tổ chức của user!")
        .result(service.getPrimaryOrg(memberId))
        .build();
  }

  @PatchMapping("/{orgId}/member/{memberId}")
  ApiResponse<Void> setPrimaryOrg(
      @PathVariable String memberId,
      @PathVariable String orgId) {
    service.setPrimaryOrg(memberId, orgId);
    return ApiResponse.<Void>builder()
        .message("Set quyền thành công trong tổ chức của user!")
        .build();
  }

  @DeleteMapping("/member/{memberId}/{scopeType}/{scopeId}")
  ApiResponse<Void> removeMembership(
      @PathVariable String memberId,
      @PathVariable String scopeType,
      @PathVariable String scopeId) {
    service.removeMembership(
        memberId,
        ScopeType.valueOf(scopeType),
        scopeId);
    return ApiResponse.<Void>builder()
        .message("Xoá member ra khỏi tổ chức thành công!")
        .build();
  }
}
