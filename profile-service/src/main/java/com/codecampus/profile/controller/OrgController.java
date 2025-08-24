package com.codecampus.profile.controller;

import com.codecampus.profile.dto.common.ApiResponse;
import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.entity.properties.exercise.AssignedOrgExercise;
import com.codecampus.profile.entity.properties.organization.CreatedOrg;
import com.codecampus.profile.entity.properties.organization.MemberOrg;
import com.codecampus.profile.service.OrganizationService;
import lombok.AccessLevel;
import lombok.Builder;
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
@Builder
@Slf4j
@RequestMapping("/org")
public class OrgController {


  OrganizationService orgService;

  @GetMapping("/my/created")
  ApiResponse<PageResponse<CreatedOrg>> getMyCreatedOrgs(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<CreatedOrg>>builder()
        .message("Tổ chức do tôi tạo")
        .result(orgService.getMyCreatedOrgs(page, size))
        .build();
  }

  @GetMapping("/my/member")
  ApiResponse<PageResponse<MemberOrg>> getMyMemberOrgs(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<MemberOrg>>builder()
        .message("Tổ chức tôi tham gia")
        .result(orgService.getMyMemberOrgs(page, size))
        .build();
  }

  @GetMapping("/my/teacher")
  ApiResponse<PageResponse<MemberOrg>> getMyTeacherOrgs(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<MemberOrg>>builder()
        .message("Tổ chức (vai trò TEACHER)")
        .result(orgService.getMyTeacherOrgs(page, size))
        .build();
  }

  @GetMapping("/my/admin")
  ApiResponse<PageResponse<MemberOrg>> getMyAdminOrgs(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<MemberOrg>>builder()
        .message("Tổ chức (vai trò ADMIN)")
        .result(orgService.getMyAdminOrgs(page, size))
        .build();
  }

  // TODO đồng bộ với phần bài tập được giao ở submission service
  @GetMapping("/{orgId}/assigned-exercises")
  ApiResponse<PageResponse<AssignedOrgExercise>> assignedExercisesOfOrg(
      @PathVariable String orgId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<AssignedOrgExercise>>builder()
        .message("Bài tập được giao cho tổ chức")
        .result(orgService.assignedExercisesOfOrg(orgId, page, size))
        .build();
  }

}
