package com.codecampus.search.controller;

import com.codecampus.search.dto.common.ApiResponse;
import com.codecampus.search.dto.common.PageResponse;
import com.codecampus.search.dto.request.UserProfileSearchRequest;
import com.codecampus.search.dto.response.UserProfileResponse;
import com.codecampus.search.service.UserProfileSearchService;
import java.time.Instant;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileSearchController {

  UserProfileSearchService userProfileSearchService;

  @GetMapping("/user-profiles/filter")
  ApiResponse<PageResponse<UserProfileResponse>> search(
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String userId,
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String email,
      @RequestParam(required = false) Set<String> roles,
      @RequestParam(required = false) Boolean active,
      @RequestParam(required = false) Boolean gender,
      @RequestParam(required = false) String city,
      @RequestParam(required = false) Integer educationMin,
      @RequestParam(required = false) Integer educationMax,
      @RequestParam(required = false) Instant createdAfter,
      @RequestParam(required = false) Instant createdBefore,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    UserProfileSearchRequest request = new UserProfileSearchRequest(
        q, userId, username, email, roles, active, gender, city,
        educationMin, educationMax, createdAfter, createdBefore, page,
        size
    );
    return ApiResponse.<PageResponse<UserProfileResponse>>builder()
        .result(userProfileSearchService.search(request))
        .message("Tìm kiếm thành công!")
        .build();
  }
}
