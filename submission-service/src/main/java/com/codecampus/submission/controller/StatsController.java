package com.codecampus.submission.controller;

import com.codecampus.submission.constant.sort.SortField;
import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.common.PageResponse;
import com.codecampus.submission.dto.response.assignment.AssignedStudentResponse;
import com.codecampus.submission.dto.response.stats.ExerciseStatRow;
import com.codecampus.submission.dto.response.stats.SummaryStat;
import com.codecampus.submission.service.AssignmentQueryService;
import com.codecampus.submission.service.StatsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/stats")
public class StatsController {

  StatsService statsService;
  AssignmentQueryService assignmentQueryService;

  /* ---------------- TEACHER: thống kê danh sách bài tập ---------------- */
  @GetMapping("/teacher/exercises")
  ApiResponse<PageResponse<ExerciseStatRow>> getTeacherExerciseStats(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "CREATED_AT") SortField sortBy,
      @RequestParam(defaultValue = "false") boolean asc
  ) {
    return ApiResponse.<PageResponse<ExerciseStatRow>>builder()
        .message("Thống kê danh sách các bài tập!")
        .result(statsService.getTeacherExerciseStats(page, size, sortBy, asc))
        .build();
  }

  /* ---------------- TEACHER: thống kê học sinh theo bài tập (đã có sẵn helper) ---------------- */
  @GetMapping("/teacher/exercises/{exerciseId}/students")
  ApiResponse<PageResponse<AssignedStudentResponse>> getAssignedStudentsForExercise(
      @PathVariable String exerciseId,
      @RequestParam(required = false) Boolean completed, // null = tất cả
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    return ApiResponse.<PageResponse<AssignedStudentResponse>>builder()
        .message("Thống kê học sinh theo bài tập!")
        .result(assignmentQueryService.getAssignedStudentsForExercise(
            exerciseId, completed, page, size))
        .build();
  }

  /* ---------------- ADMIN: thống kê danh sách bài tập ---------------- */
  @GetMapping("/admin/exercises")
  ApiResponse<PageResponse<ExerciseStatRow>> getAdminExerciseStats(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "CREATED_AT") SortField sortBy,
      @RequestParam(defaultValue = "false") boolean asc
  ) {
    return ApiResponse.<PageResponse<ExerciseStatRow>>builder()
        .message("Thống kê danh sách bài tập!")
        .result(statsService.getAdminExerciseStats(page, size, sortBy, asc))
        .build();
  }

  /* ---------------- ADMIN: số liệu tổng quan ---------------- */
  @GetMapping("/admin/summary")
  ApiResponse<SummaryStat> getAdminSummary() {
    return ApiResponse.<SummaryStat>builder()
        .message("Số liệu tổng quan")
        .result(statsService.getAdminSummary())
        .build();
  }
}