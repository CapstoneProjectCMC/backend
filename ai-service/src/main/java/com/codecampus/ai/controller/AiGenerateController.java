package com.codecampus.ai.controller;

import com.codecampus.ai.dto.common.ApiResponse;
import com.codecampus.ai.dto.request.exercise.AddQuizDetailRequest;
import com.codecampus.ai.dto.request.exercise.CreateExerciseRequest;
import com.codecampus.ai.dto.request.exercise.QuestionDto;
import com.codecampus.ai.dto.request.exercise.QuizDraft;
import com.codecampus.ai.service.ExerciseGenerationService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AiGenerateController {

    ExerciseGenerationService exerciseGenerationService;

    @GetMapping("/generate/exercise")
    public ApiResponse<CreateExerciseRequest> generateExercise(
            @RequestParam Set<String> topics) {

        return ApiResponse.<CreateExerciseRequest>builder()
                .result(exerciseGenerationService
                        .generateExercise(topics))
                .message("Gợi ý exercise!")
                .build();
    }

    @GetMapping("/generate/quiz-detail")
    public ApiResponse<AddQuizDetailRequest> generateQuizDetail(
            @RequestParam int numQuestions) {

        return ApiResponse.<AddQuizDetailRequest>builder()
                .result(exerciseGenerationService
                        .generateQuizDetail(numQuestions))
                .message("Gợi ý quiz!")
                .build();
    }

    @GetMapping("/generate/question")
    public ApiResponse<QuestionDto> generateQuestion(
            @RequestParam(defaultValue = "1") int orderInQuiz) {

        return ApiResponse.<QuestionDto>builder()
                .result(exerciseGenerationService
                        .generateQuestion(orderInQuiz))
                .message("Gợi ý câu hỏi!")
                .build();
    }

    @GetMapping("/draft/quiz")
    public ApiResponse<QuizDraft> generateQuizDraft(
            @RequestParam Set<String> topics,
            @RequestParam(defaultValue = "5") int numQuestions) {

        return ApiResponse.<QuizDraft>builder()
                .result(exerciseGenerationService
                        .generateQuizDraft(topics, numQuestions))
                .message("Gợi ý bản nháp quiz!")
                .build();
    }
}
