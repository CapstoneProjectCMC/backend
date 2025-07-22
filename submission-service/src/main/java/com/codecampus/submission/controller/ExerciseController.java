package com.codecampus.submission.controller;

import com.codecampus.submission.constant.sort.SortField;
import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.common.PageResponse;
import com.codecampus.submission.dto.request.CreateExerciseRequest;
import com.codecampus.submission.dto.request.UpdateExerciseRequest;
import com.codecampus.submission.dto.request.quiz.CreateQuizExerciseRequest;
import com.codecampus.submission.dto.response.quiz.ExerciseQuizResponse;
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

        exerciseService.createExercise(request);

        return ApiResponse.<Void>builder()
                .message("Tạo bài tập thành công!")
                .build();
    }

    @PostMapping("/exercise/quiz")
    ApiResponse<Void> createQuizExercise(
            @RequestBody @Valid CreateQuizExerciseRequest request) {

        exerciseService.createQuizExercise(request);

        return ApiResponse.<Void>builder()
                .message("Tạo bài tập quiz thành công!")
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
    ApiResponse<PageResponse<ExerciseQuizResponse>> getAllExercises(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "CREATED_AT") SortField sortBy,
            @RequestParam(defaultValue = "false") boolean asc
    ) {
        return ApiResponse.<PageResponse<ExerciseQuizResponse>>builder()
                .result(exerciseService.getAllExercises(
                        page, size,
                        sortBy, asc)
                )
                .message("Lấy toàn bộ exercise thành công!")
                .build();
    }

    @GetMapping("/exercises/self")
    ApiResponse<PageResponse<ExerciseQuizResponse>> getExercisesOf(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "CREATED_AT") SortField sortBy,
            @RequestParam(defaultValue = "false") boolean asc) {
        return ApiResponse.<PageResponse<ExerciseQuizResponse>>builder()
                .result(exerciseService.getExercisesOf(
                        page, size,
                        sortBy, asc)
                )
                .message("Exercise của giáo viên!")
                .build();
    }

    @GetMapping("/exercise/{id}")
    ApiResponse<ExerciseQuizDetailResponse> getExercise(
            @PathVariable String id,
            @RequestParam(defaultValue = "1") int qPage,
            @RequestParam(defaultValue = "5") int qSize,
            @RequestParam(defaultValue = "ORDER_IN_QUIZ") SortField qSortBy,
            @RequestParam(defaultValue = "false") boolean qAsc) {
        return ApiResponse.<ExerciseQuizDetailResponse>builder()
                .result(exerciseService.getExerciseDetail(
                        id,
                        qPage, qSize,
                        qSortBy, qAsc))
                .message("Chi tiết exercise!")
                .build();
    }
}
