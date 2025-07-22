package com.codecampus.ai.controller;

import com.codecampus.ai.constant.exercise.ExerciseType;
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
            @RequestParam Set<String> topics,
            @RequestParam ExerciseType exerciseType) {

        return ApiResponse.<CreateExerciseRequest>builder()
                .result(exerciseGenerationService
                        .generateExercise(topics, exerciseType))
                .message("Gợi ý exercise!")
                .build();
    }

    @GetMapping("/generate/quiz-detail")
    public ApiResponse<AddQuizDetailRequest> generateQuizDetail(
            @RequestParam String exerciseTitle,
            @RequestParam String exerciseDescription,
            @RequestParam int numQuestions,
            @RequestParam Set<String> topics) {

        return ApiResponse.<AddQuizDetailRequest>builder()
                .result(exerciseGenerationService
                        .generateQuizDetail(
                                exerciseTitle,
                                exerciseDescription,
                                numQuestions,
                                topics)
                )
                .message("Gợi ý quiz!")
                .build();
    }

    @GetMapping("/generate/question")
    public ApiResponse<QuestionDto> generateQuestion(
            @RequestParam(defaultValue = "1") int orderInQuiz,
            @RequestParam Set<String> topicsQuiz) {

        return ApiResponse.<QuestionDto>builder()
                .result(exerciseGenerationService
                        .generateQuestion(orderInQuiz, topicsQuiz))
                .message("Gợi ý câu hỏi!")
                .build();
    }

    @GetMapping("/generate/draft/quiz")
    public ApiResponse<QuizDraft> generateQuizDraft(
            @RequestParam Set<String> exerciseTopics,
            @RequestParam ExerciseType exerciseType,
            @RequestParam(defaultValue = "5") int numQuestions) {

        return ApiResponse.<QuizDraft>builder()
                .result(exerciseGenerationService
                        .generateQuizDraft(
                                exerciseTopics,
                                exerciseType,
                                numQuestions))
                .message("Gợi ý bản nháp quiz!")
                .build();
    }
}
