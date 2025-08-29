package com.codecampus.organization.controller;

import com.codecampus.organization.dto.common.ApiResponse;
import com.codecampus.organization.dto.request.CreateBlockRequest;
import com.codecampus.organization.dto.response.BlockResponse;
import com.codecampus.organization.service.OrganizationBlockService;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationBlockController {
  OrganizationBlockService service;

  @PostMapping("/grade")
  public ApiResponse<BlockResponse> createGrade(
      @RequestBody CreateBlockRequest request) {
    return ApiResponse.<BlockResponse>builder()
        .result(service.createGrade(request))
        .build();
  }

  @GetMapping("/{orgId}/grades")
  public ApiResponse<List<BlockResponse>> getAllGradesByOrganization(
      @PathVariable String orgId) {
    return ApiResponse.<List<BlockResponse>>builder()
        .result(service.getAllGradesByOrganization(orgId))
        .build();
  }
}
