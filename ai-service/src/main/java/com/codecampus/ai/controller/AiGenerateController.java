//package com.codecampus.ai.controller;
//
//import com.codecampus.ai.dto.common.ApiResponse;
//import com.codecampus.ai.dto.response.ExerciseResponse;
//import com.codecampus.ai.dto.response.QuestionResponse;
//import com.codecampus.ai.service.ExerciseGenerationService;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.coyote.BadRequestException;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@Slf4j
//@Builder
//@RestController
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class AiGenerateController {
//
//    ExerciseGenerationService exerciseGenerationService;
//
//    @GetMapping("/generate/draft/quiz")
//    public ApiResponse<ExerciseResponse> generateQuizExercise() {
//
//        return ApiResponse.<ExerciseResponse>builder()
//                .result(exerciseGenerationService
//                        .generateQuizExercise())
//                .message("Gợi ý bản nháp quiz!")
//                .build();
//    }
//
//    @GetMapping("/generate/{quizId}/question")
//    public ApiResponse<QuestionResponse> generateQuestion()
//            throws BadRequestException {
//
//        return ApiResponse.<QuestionResponse>builder()
//                .result(exerciseGenerationService
//                        .generateQuestion()
//                )
//                .message("Gợi ý câu hỏi!")
//                .build();
//    }
//}
