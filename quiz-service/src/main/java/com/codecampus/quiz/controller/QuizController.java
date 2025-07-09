package com.codecampus.quiz.controller;

import com.codecampus.quiz.dto.common.ApiResponse;
import com.codecampus.quiz.grpc.LoadQuizResponse;
import com.codecampus.quiz.grpc.QuizExerciseDto;
import com.codecampus.quiz.grpc.SubmitQuizRequest;
import com.codecampus.quiz.grpc.SubmitQuizResponse;
import com.codecampus.quiz.service.QuizService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
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
class QuizController {

    QuizService quizService;

    @GetMapping("/{quizId}")
    ApiResponse<QuizExerciseDto> getQuiz(
            @PathVariable String quizId) {
        return ApiResponse.<QuizExerciseDto>builder()
                .result(quizService.getQuizExerciseDto(quizId))
                .message("Lấy quiz thành công!")
                .build();
    }

    @GetMapping("/{quizId}/load")
    ApiResponse<LoadQuizResponse> loadQuiz(
            @PathVariable String quizId,
            @RequestParam("student") String studentId) {

        return ApiResponse.<LoadQuizResponse>builder()
                .result(quizService.loadQuiz(quizId, studentId))
                .message("Load quiz thành công!")
                .build();
    }

    @PostMapping("/{quizId}/submit")
    ApiResponse<SubmitQuizResponse> submitQuiz(
            @PathVariable String quizId,
            @RequestBody SubmitQuizRequest request) {

        // Bảo đảm path id khớp body
        Assert.isTrue(quizId.equals(request.getExerciseId()),
                "exerciseId mismatch");

        return ApiResponse.<SubmitQuizResponse>builder()
                .result(quizService.submitQuiz(request))
                .message("Nộp bài thành công!")
                .build();
    }
}
