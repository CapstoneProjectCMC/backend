package com.codecampus.organization.controller;

import com.codecampus.organization.dto.common.ApiResponse;
import com.codecampus.organization.dto.request.CreateOrganizationForm;
import com.codecampus.organization.dto.request.UpdateOrganizationForm;
import com.codecampus.organization.dto.response.OrganizationResponse;
import com.codecampus.organization.service.OrganizationService;
import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationController {
  OrganizationService service;

  @PostMapping(
      value = "/organization",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ApiResponse<OrganizationResponse> createOrganization(
      @ModelAttribute CreateOrganizationForm request) {
    return ApiResponse.<OrganizationResponse>builder()
        .message("Thêm tổ chức thành công!")
        .result(service.createOrganization(request))
        .build();
  }

  @PatchMapping(value = "/organization/{orgId}",
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  ApiResponse<OrganizationResponse> updateOrganization(
      @PathVariable("orgId") String orgId,
      @ModelAttribute UpdateOrganizationForm request) {
    return ApiResponse.<OrganizationResponse>builder()
        .message("Sửa tổ chức thành công!")
        .result(service.updateOrganization(orgId, request))
        .build();
  }

  @DeleteMapping("/organization/{orgId}")
  ApiResponse<Void> deleteOrganization(
      @PathVariable("orgId") String orgId) {
    service.deleteOrganization(orgId);
    return ApiResponse.<Void>builder()
        .message("Xoá tổ chức thành công!")
        .build();
  }

  @GetMapping("/organizations")
  ApiResponse<List<OrganizationResponse>> getAllOrganizations() {
    return ApiResponse.<List<OrganizationResponse>>builder()
        .result(service.getAllOrganizations())
        .build();
  }

  @GetMapping("/organization/{orgId}")
  ApiResponse<OrganizationResponse> getOrganizationById(
      @PathVariable("orgId") String orgId) {
    return ApiResponse.<OrganizationResponse>builder()
        .result(service.getOrganizationById(orgId))
        .build();
  }
}
