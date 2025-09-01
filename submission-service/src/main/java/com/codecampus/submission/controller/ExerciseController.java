package com.codecampus.submission.controller;

import com.codecampus.submission.constant.sort.SortField;
import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.common.PageResponse;
import com.codecampus.submission.dto.request.CreateExerciseRequest;
import com.codecampus.submission.dto.request.UpdateExerciseRequest;
import com.codecampus.submission.dto.request.coding.CreateCodingExerciseRequest;
import com.codecampus.submission.dto.request.quiz.CreateQuizExerciseRequest;
import com.codecampus.submission.dto.response.ExerciseResponse;
import com.codecampus.submission.dto.response.coding.coding_detail.ExerciseCodingDetailResponse;
import com.codecampus.submission.dto.response.quiz.quiz_detail.ExerciseQuizDetailResponse;
import com.codecampus.submission.service.ExerciseService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseController {

  ExerciseService exerciseService;

  @PostMapping("/exercise")
  ApiResponse<Void> createExercise(
      @RequestBody @Valid CreateExerciseRequest request) {

    exerciseService.createExercise(request, false);

    return ApiResponse.<Void>builder()
        .message("Tạo bài tập thành công!")
        .build();
  }

  @PostMapping("/exercise/quiz")
  ApiResponse<Void> createQuizExercise(
      @RequestBody @Valid CreateQuizExerciseRequest request) {

    exerciseService.createQuizExercise(request, false);

    return ApiResponse.<Void>builder()
        .message("Tạo bài tập quiz thành công!")
        .build();
  }

  @PostMapping("/exercise/coding")
  ApiResponse<Void> createCodingExercise(
      @RequestBody @Valid CreateCodingExerciseRequest request) {

    exerciseService.createCodingExercise(request, false);

    return ApiResponse.<Void>builder()
        .message("Tạo bài tập coding thành công!")
        .build();
  }

  @PatchMapping("/exercise/{id}")
  ApiResponse<Void> updateExercise(
      @PathVariable String id,
      @RequestBody UpdateExerciseRequest request) {

    exerciseService.updateExercise(id, request);

    return ApiResponse.<Void>builder()
        .message("Sửa exercise thành công!")
        .build();
  }

  @DeleteMapping("/exercise/{id}")
  ApiResponse<Void> softDeleteExercise(
      @PathVariable String id) {
    exerciseService.softDeleteExercise(id);
    return ApiResponse.<Void>builder()
        .message("Đã xoá bài tập!")
        .build();
  }

  @GetMapping("/exercises")
  ApiResponse<PageResponse<ExerciseResponse>> getAllExercises(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "CREATED_AT") SortField sortBy,
      @RequestParam(defaultValue = "false") boolean asc
  ) {
    return ApiResponse.<PageResponse<ExerciseResponse>>builder()
        .result(exerciseService.getAllExercises(
            page, size,
            sortBy, asc)
        )
        .message("Lấy toàn bộ exercise thành công!")
        .build();
  }

  @GetMapping("/exercises/self")
  ApiResponse<PageResponse<ExerciseResponse>> getExercisesOf(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "CREATED_AT") SortField sortBy,
      @RequestParam(defaultValue = "false") boolean asc) {
    return ApiResponse.<PageResponse<ExerciseResponse>>builder()
        .result(exerciseService.getExercisesOf(
            page, size,
            sortBy, asc)
        )
        .message("Exercise của giáo viên!")
        .build();
  }

  @GetMapping("/exercise/quiz/{id}")
  ApiResponse<ExerciseQuizDetailResponse> getQuizExerciseDetail(
      @PathVariable String id,
      @RequestParam(defaultValue = "1") int qPage,
      @RequestParam(defaultValue = "5") int qSize,
      @RequestParam(defaultValue = "ORDER_IN_QUIZ") SortField qSortBy,
      @RequestParam(defaultValue = "false") boolean qAsc) {
    return ApiResponse.<ExerciseQuizDetailResponse>builder()
        .result(exerciseService.getQuizExerciseDetail(
            id,
            qPage, qSize,
            qSortBy, qAsc))
        .message("Chi tiết exercise!")
        .build();
  }

  // controller/ExerciseController.java  (thêm cuối file)
  @GetMapping("/exercise/coding/{id}")
  ApiResponse<ExerciseCodingDetailResponse> getCodingExerciseDetail(
      @PathVariable String id,
      @RequestParam(defaultValue = "1") int tcPage,
      @RequestParam(defaultValue = "5") int tcSize,
      @RequestParam(defaultValue = "CREATED_AT") SortField tcSortBy,
      @RequestParam(defaultValue = "false") boolean tcAsc) {

    return ApiResponse.<ExerciseCodingDetailResponse>builder()
        .result(exerciseService.getCodingExerciseDetail(
            id, tcPage, tcSize, tcSortBy, tcAsc))
        .message("Chi tiết exercise coding!")
        .build();
  }

}
