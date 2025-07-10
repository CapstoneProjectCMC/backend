package com.codecampus.submission.controller;

import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.request.CreateExerciseRequest;
import com.codecampus.submission.dto.request.UpdateExerciseRequest;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.service.ExerciseService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseController {

    ExerciseService exerciseService;

    @PostMapping("/exercise")
    ApiResponse<Exercise> createExercise(
            @RequestBody @Valid CreateExerciseRequest request) {
        return ApiResponse.<Exercise>builder()
                .result(exerciseService.createExercise(request))
                .message("Tạo bài tập thành công!")
                .build();
    }


    @PatchMapping("/exercise/{id}")
    public ApiResponse<Exercise> updateExercise(
            @PathVariable String id,
            @RequestBody UpdateExerciseRequest request) {
        return ApiResponse.<Exercise>builder()
                .result(exerciseService.updateExercise(id, request))
                .message("Sửa exercise thành công!")
                .build();
    }

    @GetMapping("/exercises")
    ApiResponse<List<Exercise>> getAllExercises() {
        return ApiResponse.<List<Exercise>>builder()
                .result(exerciseService.getAllExercises())
                .message("Lấy toàn bộ exercise thành công!")
                .build();
    }

    @GetMapping("/exercises/self")
    ApiResponse<List<Exercise>> getExercisesOf() {
        return ApiResponse.<List<Exercise>>builder()
                .result(exerciseService.getExercisesOf())
                .message("Exercise của giáo viên!")
                .build();
    }

    @GetMapping("/exercise/{id}")
    ApiResponse<Exercise> getExercise(@PathVariable String id) {
        return ApiResponse.<Exercise>builder()
                .result(exerciseService.getExerciseOrThrow(id))
                .message("Chi tiết exercise!")
                .build();
    }
}
