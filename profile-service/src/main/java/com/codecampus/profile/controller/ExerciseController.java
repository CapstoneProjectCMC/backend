package com.codecampus.profile.controller;

import com.codecampus.profile.dto.common.ApiResponse;
import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.entity.properties.exercise.CompletedExercise;
import com.codecampus.profile.entity.properties.exercise.CreatedExercise;
import com.codecampus.profile.entity.properties.exercise.SavedExercise;
import com.codecampus.profile.service.ExerciseService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class ExerciseController {
    ExerciseService exerciseService;

    /* ---- SAVE / UNSAVE ------------------------------------------------- */

    @PostMapping("/exercise/{exerciseId}/save")
    ApiResponse<Void> saveExercise(@PathVariable String exerciseId) {
        exerciseService.saveExercise(exerciseId);
        return ApiResponse.<Void>builder()
                .message("Đã lưu bài tập")
                .build();
    }

    @DeleteMapping("/exercise/{exerciseId}/save")
    ApiResponse<Void> unsave(@PathVariable String exerciseId) {
        exerciseService.unsaveExercise(exerciseId);
        return ApiResponse.<Void>builder()
                .message("Đã hủy lưu bài tập")
                .build();
    }

    /* ---- PAGING QUERIES ------------------------------------------------ */

    @GetMapping("/exercises/saved")
    ApiResponse<PageResponse<SavedExercise>> getSavedExercises(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.<PageResponse<SavedExercise>>builder()
                .message("Bài tập đã lưu")
                .result(exerciseService.getSavedExercises(page, size))
                .build();
    }

    @GetMapping("/exercises/completed")
    ApiResponse<PageResponse<CompletedExercise>> getCompletedExercises(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.<PageResponse<CompletedExercise>>builder()
                .message("Bài tập đã hoàn thành")
                .result(exerciseService.getCompletedExercises(page, size))
                .build();
    }

    @GetMapping("/exercises/created")
    ApiResponse<PageResponse<CreatedExercise>> getCreatedExercises(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ApiResponse.<PageResponse<CreatedExercise>>builder()
                .message("Bài tập do tôi tạo")
                .result(exerciseService.getCreatedExercises(page, size))
                .build();
    }
}
