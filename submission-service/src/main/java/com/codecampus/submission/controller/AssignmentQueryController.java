package com.codecampus.submission.controller;

import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.common.PageResponse;
import com.codecampus.submission.dto.response.assignment.AssignedStudentResponse;
import com.codecampus.submission.dto.response.assignment.MyAssignmentResponse;
import com.codecampus.submission.service.AssignmentQueryService;
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
public class AssignmentQueryController {

  AssignmentQueryService assignmentQueryService;

  @GetMapping("/assignments/self")
  ApiResponse<PageResponse<MyAssignmentResponse>> myAssignments(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {
    return ApiResponse.<PageResponse<MyAssignmentResponse>>builder()
        .result(assignmentQueryService.getAssignmentsForStudent(
            page, size))
        .message("Danh sách bài tập được giao cho bạn!")
        .build();
  }

  @GetMapping("/assignments/{exerciseId}")
  public PageResponse<AssignedStudentResponse> getAssignedStudentsForExercise(
      @PathVariable String exerciseId,
      @RequestParam(required = false) Boolean completed,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size) {

    return assignmentQueryService.getAssignedStudentsForExercise(
        exerciseId, completed,
        page, size);
  }
}
