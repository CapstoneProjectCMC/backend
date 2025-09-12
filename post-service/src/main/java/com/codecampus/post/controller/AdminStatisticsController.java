package com.codecampus.post.controller;

import com.codecampus.post.dto.common.ApiResponse;
import com.codecampus.post.dto.common.PageResponse;
import com.codecampus.post.dto.response.stats.AdminPostStatDto;
import com.codecampus.post.dto.response.stats.HashtagStatDto;
import com.codecampus.post.dto.response.stats.UserPostLeaderboardDto;
import com.codecampus.post.service.AdminStatisticsService;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stats")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminStatisticsController {

  AdminStatisticsService service;

  // Tổng quan bài viết (lọc + sắp xếp)
  @GetMapping("/posts/overview")
  ApiResponse<PageResponse<AdminPostStatDto>> overview(
      @RequestParam(required = false) String orgId,
      @RequestParam(required = false) String userId,
      @RequestParam(required = false) String postType,
      @RequestParam(required = false) Boolean is,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
      @RequestParam(required = false) String q,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      // sortField: createdAt | lastActivityAt | commentCount | upvoteCount | downvoteCount
      @RequestParam(defaultValue = "lastActivityAt") String sortField,
      @RequestParam(defaultValue = "DESC") String sortDir
  ) {
    Sort sort = Sort.by(sortField);
    sort =
        "DESC".equalsIgnoreCase(sortDir) ? sort.descending() : sort.ascending();

    return ApiResponse.<PageResponse<AdminPostStatDto>>builder()
        .message("Thống kê tổng quan bài viết thành công!")
        .result(
            service.overview(orgId, userId, postType, is, start, end, q,
                page, size, sort))
        .build();
  }

  // Top bài viết theo số bình luận
  @GetMapping("/posts/top-commented")
  ApiResponse<PageResponse<AdminPostStatDto>> topCommented(
      @RequestParam(required = false) String orgId,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return ApiResponse.<PageResponse<AdminPostStatDto>>builder()
        .message("Thống kê top bài viết theo bình luận thành công!")
        .result(service.topCommented(orgId, start, end, page, size))
        .build();
  }

  // Top bài viết theo reaction (score = up - down)
  @GetMapping("/posts/top-reacted")
  ApiResponse<PageResponse<AdminPostStatDto>> topReacted(
      @RequestParam(required = false) String orgId,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return ApiResponse.<PageResponse<AdminPostStatDto>>builder()
        .message("Thống kê top bài viết theo reaction thành công!")
        .result(service.topReacted(orgId, start, end, page, size))
        .build();
  }

  // BXH người dùng theo số bài viết (kèm comment/reaction tạo)
  @GetMapping("/users/top-posters")
  ApiResponse<PageResponse<UserPostLeaderboardDto>> topPosters(
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return ApiResponse.<PageResponse<UserPostLeaderboardDto>>builder()
        .message("Bảng xếp hạng người dùng theo bài viết thành công!")
        .result(service.topPosters(start, end, page, size))
        .build();
  }

  // Thống kê hashtag
  @GetMapping("/hashtags")
  ApiResponse<PageResponse<HashtagStatDto>> hashtagStats(
      @RequestParam(required = false) String orgId,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return ApiResponse.<PageResponse<HashtagStatDto>>builder()
        .message("Thống kê hashtag thành công!")
        .result(service.hashtagStats(orgId, start, end, page, size))
        .build();
  }
}