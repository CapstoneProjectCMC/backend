package com.codecampus.search.controller;

import com.codecampus.search.dto.common.ApiResponse;
import com.codecampus.search.dto.common.PageResponse;
import com.codecampus.search.dto.request.PostSearchRequest;
import com.codecampus.search.dto.response.PostSearchResponse;
import com.codecampus.search.helper.AuthenticationHelper;
import com.codecampus.search.service.PostSearchService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostSearchController {

  PostSearchService postSearchService;

  @GetMapping("/posts/filter")
  ApiResponse<PageResponse<PostSearchResponse>> search(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String postType,
      @RequestParam(required = false) Boolean isPublic,
      @RequestParam(required = false) String status,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    PostSearchRequest req =
        new PostSearchRequest(q, null, postType, isPublic, status, page, size);

    String viewerId = AuthenticationHelper.getUserId();

    return ApiResponse.<PageResponse<PostSearchResponse>>builder()
        .result(postSearchService.search(req, viewerId))
        .message("Tìm kiếm Post thành công!")
        .build();
  }
}