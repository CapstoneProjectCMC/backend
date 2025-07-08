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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/exercise")
public class ExerciseController {

    ExerciseService exerciseService;

    @PostMapping
    ApiResponse<Exercise> createExercise(
            @RequestBody @Valid CreateExerciseRequest request) {
        return ApiResponse.<Exercise>builder()
                .result(exerciseService.createExercise(request))
                .message("Tạo bài tập thành công!")
                .build();
    }


    @PatchMapping("/{id}")
    public ApiResponse<Exercise> updateExercise(
            @PathVariable String id,
            @RequestBody UpdateExerciseRequest request) {
        return ApiResponse.<Exercise>builder()
                .result(exerciseService.updateExercise(id, request))
                .message("Sửa exercise thành công!")
                .build();
    }
}
