package com.codecampus.organization.controller;

import com.codecampus.organization.dto.common.ApiResponse;
import com.codecampus.organization.dto.response.IdNameResponse;
import com.codecampus.organization.service.OrganizationService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/internal")
public class InternalOrganizationController {

  OrganizationService organizationService;

  @GetMapping("/organization/resolve")
  ApiResponse<IdNameResponse> internalResolveOrganizationByName(
      @RequestParam("name") String name) {
    return ApiResponse.<IdNameResponse>builder()
        .result(organizationService.resolveOrganizationByName(name))
        .build();
  }
}
