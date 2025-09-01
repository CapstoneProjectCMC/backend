package com.codecampus.organization.controller;

import com.codecampus.organization.dto.common.ApiResponse;
import com.codecampus.organization.dto.common.PageResponse;
import com.codecampus.organization.service.ExerciseService;
import dtos.ExerciseSummary;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseController {
  ExerciseService service;

  @GetMapping("/{orgId}/exercises")
  public ApiResponse<PageResponse<ExerciseSummary>> listExercisesOfOrg(
      @PathVariable String orgId,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ApiResponse.<PageResponse<ExerciseSummary>>builder()
        .result(service.listExercisesOfOrg(orgId, page, size))
        .build();
  }
}
