package com.codecampus.organization.controller;

import com.codecampus.organization.dto.common.ApiResponse;
import com.codecampus.organization.dto.common.PageResponse;
import com.codecampus.organization.service.PostService;
import dtos.PostSummary;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {
  PostService service;

  @GetMapping("/{orgId}/posts")
  public ApiResponse<PageResponse<PostSummary>> listPostsOfOrg(
      @PathVariable String orgId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ApiResponse.<PageResponse<PostSummary>>builder()
        .result(service.listPostsOfOrg(orgId, page, size))
        .build();
  }
}