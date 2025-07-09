package com.codecampus.submission.controller;

import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.request.quiz.AddQuizDetailRequest;
import com.codecampus.submission.dto.request.quiz.OptionDto;
import com.codecampus.submission.dto.request.quiz.QuestionDto;
import com.codecampus.submission.dto.request.quiz.UpdateOptionRequest;
import com.codecampus.submission.dto.request.quiz.UpdateQuestionRequest;
import com.codecampus.submission.entity.Option;
import com.codecampus.submission.entity.Question;
import com.codecampus.submission.entity.QuizDetail;
import com.codecampus.submission.service.ExerciseService;
import com.codecampus.submission.service.QuizService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
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
@RequestMapping("/quiz")
public class QuizController {
    ExerciseService exerciseService;
    QuizService quizService;

    @PostMapping("/exercise/{exerciseId}/quiz-detail")
    ApiResponse<QuizDetail> addQuizDetail(
            @PathVariable("exerciseId") String exerciseId,
            @RequestBody @Valid AddQuizDetailRequest addQuizRequest) {
        return ApiResponse.<QuizDetail>builder()
                .result(quizService
                        .addQuizDetail(exerciseId, addQuizRequest))
                .message("Tạo Quiz thành công!")
                .build();
    }

    @PostMapping("/{exerciseId}/question")
    ApiResponse<Question> addQuestion(
            @PathVariable("exerciseId") String exerciseId,
            @RequestBody @Valid QuestionDto questionDto)
            throws BadRequestException {
        return ApiResponse.<Question>builder()
                .result(quizService.addQuestion(exerciseId, questionDto))
                .message("Thêm câu hỏi cho quiz thành công!")
                .build();
    }

    @PostMapping("/question/{questionId}/option")
    ApiResponse<Option> addOption(
            @PathVariable String questionId,
            @RequestBody @Valid OptionDto request) {

        return ApiResponse.<Option>builder()
                .result(quizService.addOption(questionId, request))
                .message("Thêm option thành công!")
                .build();
    }

    @PatchMapping("/{exerciseId}/question/{questionId}")
    ApiResponse<Question> updateQuestion(
            @PathVariable String exerciseId,
            @PathVariable String questionId,
            @RequestBody UpdateQuestionRequest request) {
        return ApiResponse.<Question>builder()
                .result(quizService.updateQuestion(exerciseId, questionId,
                        request))
                .message("Sửa question thành công!")
                .build();
    }

    @PatchMapping("/question/option/{optionId}")
    ApiResponse<Option> updateOption(
            @PathVariable String optionId,
            @RequestBody UpdateOptionRequest request) {
        return ApiResponse.<Option>builder()
                .result(quizService.updateOption(optionId, request))
                .message("Sửa option thành công!")
                .build();
    }
}
