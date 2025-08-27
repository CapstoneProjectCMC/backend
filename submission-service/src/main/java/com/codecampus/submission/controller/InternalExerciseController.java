package com.codecampus.submission.controller;

import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.data.ExerciseSummaryDto;
import com.codecampus.submission.dto.request.CreateExerciseRequest;
import com.codecampus.submission.dto.request.coding.CreateCodingExerciseRequest;
import com.codecampus.submission.dto.request.quiz.CreateQuizExerciseRequest;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.helper.ExerciseHelper;
import com.codecampus.submission.service.ExerciseService;
import dtos.ExerciseStatusDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/internal")
public class InternalExerciseController {

  ExerciseService exerciseService;
  ExerciseHelper exerciseHelper;


  @PostMapping("/exercise")
  ApiResponse<Exercise> internalCreateExercise(
      @RequestBody @Valid CreateExerciseRequest request) {

    return ApiResponse.<Exercise>builder()
        .message("Tạo bài tập thành công!")
        .result(exerciseService
            .createExercise(request, true))
        .build();
  }

  @PostMapping("/exercise/quiz")
  ApiResponse<Exercise> internalCreateQuizExercise(
      @RequestBody @Valid CreateQuizExerciseRequest request) {

    return ApiResponse.<Exercise>builder()
        .message("Tạo bài tập quiz thành công!")
        .result(exerciseService
            .createQuizExercise(request, true))
        .build();
  }

  @PostMapping("/exercise/coding")
  ApiResponse<Exercise> internalCreateCodingExercise(
      @RequestBody @Valid CreateCodingExerciseRequest request) {

    return ApiResponse.<Exercise>builder()
        .message("Tạo bài tập coding thành công!")
        .result(exerciseService
            .createCodingExercise(request, true))
        .build();
  }

  @GetMapping("/exercise/{exerciseId}/summary")
  ApiResponse<ExerciseSummaryDto> internalGetExerciseSummary(
      @PathVariable @NotBlank String exerciseId) {
    return ApiResponse.<ExerciseSummaryDto>builder()
        .message("Get exercise summary thành công!")
        .result(exerciseService.getExerciseSummary(exerciseId))
        .build();
  }

  @GetMapping("/exercise/{exerciseId}/status")
  ApiResponse<ExerciseStatusDto> internalGetExerciseStatus(
      @PathVariable @NotBlank String exerciseId,
      @RequestParam("studentId") @NotBlank String studentId) {
    return ApiResponse.<ExerciseStatusDto>builder()
        .message("Get trạng thái exercise thành công!")
        .result(exerciseService.getExerciseStatus(exerciseId, studentId))
        .build();
  }
}
