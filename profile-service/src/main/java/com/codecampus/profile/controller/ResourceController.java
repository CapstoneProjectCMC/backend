package com.codecampus.profile.controller;

import com.codecampus.profile.dto.common.ApiResponse;
import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.dto.request.ReportRequest;
import com.codecampus.profile.entity.properties.resource.SavedResource;
import com.codecampus.profile.service.ResourceService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
@RequestMapping("/resource")
public class ResourceController {

  ResourceService resourceService;

  @PostMapping("/{fileId}/save")
  ApiResponse<Void> saveResource(
      @PathVariable String fileId) {
    resourceService.saveResource(fileId);
    return ApiResponse.<Void>builder()
        .message("Đã lưu tài nguyên")
        .build();
  }

  @PostMapping("/{fileId}/report")
  ApiResponse<Void> reportResource(@PathVariable String fileId,
                                   @RequestBody ReportRequest body) {
    resourceService.reportResource(fileId, body.reason());
    return ApiResponse.<Void>builder()
        .message("Đã báo cáo tài nguyên")
        .build();
  }

  @GetMapping("/saved/lectures")
  ApiResponse<PageResponse<SavedResource>> getSavedLectures(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<SavedResource>>builder()
        .message("Bài giảng đã lưu")
        .result(resourceService.getSavedLectures(page, size))
        .build();
  }

  @GetMapping("/saved/textbooks")
  ApiResponse<PageResponse<SavedResource>> getSavedTextbooks(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<SavedResource>>builder()
        .message("Giáo trình đã lưu")
        .result(resourceService.getSavedTextbooks(page, size))
        .build();
  }
}
