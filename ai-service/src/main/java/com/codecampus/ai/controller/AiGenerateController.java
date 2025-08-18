package com.codecampus.ai.controller;

import com.codecampus.ai.dto.common.ApiResponse;
import com.codecampus.ai.dto.request.coding.GenerateCodingPromptIn;
import com.codecampus.ai.dto.request.coding.GenerateTestCasesPromptIn;
import com.codecampus.ai.dto.request.quiz.GenerateQuizPromptIn;
import com.codecampus.ai.dto.request.quiz.QuestionPromptIn;
import com.codecampus.ai.dto.response.ExerciseResponse;
import com.codecampus.ai.dto.response.QuestionResponse;
import com.codecampus.ai.dto.response.TestCaseResponse;
import com.codecampus.ai.service.ExerciseGenerationService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AiGenerateController {

    ExerciseGenerationService exerciseGenerationService;

    @PostMapping("/generate/quiz")
    public ApiResponse<ExerciseResponse> generateQuizExercise(
            @RequestBody GenerateQuizPromptIn generateQuizPromptIn) {

        return ApiResponse.<ExerciseResponse>builder()
                .result(exerciseGenerationService
                        .generateQuizExercise(generateQuizPromptIn))
                .message("Gợi ý bản nháp quiz!")
                .build();
    }

    @PostMapping("/generate/question")
    public ApiResponse<QuestionResponse> generateQuestion(
            @RequestBody QuestionPromptIn questionPromptIn)
            throws BadRequestException {

        return ApiResponse.<QuestionResponse>builder()
                .result(exerciseGenerationService
                        .generateQuestion(questionPromptIn)
                )
                .message("Gợi ý câu hỏi!")
                .build();
    }

    @PostMapping("/generate/coding")
    public ApiResponse<ExerciseResponse> generateCodingExercise(
            @RequestBody GenerateCodingPromptIn in) {
        return ApiResponse.<ExerciseResponse>builder()
                .result(exerciseGenerationService.generateCodingExercise(in))
                .message("Gợi ý bản nháp coding!")
                .build();
    }

    @PostMapping("/generate/coding/test-cases")
    public ApiResponse<List<TestCaseResponse>> generateMoreTestCases(
            @RequestBody GenerateTestCasesPromptIn in)
            throws BadRequestException {
        return ApiResponse.<List<TestCaseResponse>>builder()
                .result(exerciseGenerationService.generateTestCases(in))
                .message("Đã sinh thêm testcases!")
                .build();
    }
}
