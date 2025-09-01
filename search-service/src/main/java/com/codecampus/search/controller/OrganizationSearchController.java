package com.codecampus.search.controller;

import com.codecampus.search.dto.common.ApiResponse;
import com.codecampus.search.dto.common.PageResponse;
import com.codecampus.search.dto.response.OrganizationSearchResponse;
import com.codecampus.search.service.OrganizationSearchService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationSearchController {

  OrganizationSearchService service;

  @GetMapping("/organizations/filter")
  ApiResponse<PageResponse<OrganizationSearchResponse>> search(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "true") boolean includeBlocks,
      @RequestParam(defaultValue = "1") int blocksPage,
      @RequestParam(defaultValue = "10") int blocksSize,
      @RequestParam(defaultValue = "1") int membersPage,
      @RequestParam(defaultValue = "10") int membersSize,
      @RequestParam(defaultValue = "true") boolean activeOnlyMembers,
      @RequestParam(defaultValue = "true") boolean includeUnassigned
  ) {
    return ApiResponse.<PageResponse<OrganizationSearchResponse>>builder()
        .result(service.search(q, status, page, size, includeBlocks,
            blocksPage, blocksSize, membersPage, membersSize,
            activeOnlyMembers, includeUnassigned))
        .message("Tìm kiếm Organization thành công!")
        .build();
  }
}