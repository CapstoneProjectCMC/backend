package com.codecampus.submission.controller;

import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.request.quiz.AddQuizDetailRequest;
import com.codecampus.submission.dto.request.quiz.OptionDto;
import com.codecampus.submission.dto.request.quiz.QuestionDto;
import com.codecampus.submission.entity.Option;
import com.codecampus.submission.entity.Question;
import com.codecampus.submission.entity.QuizDetail;
import com.codecampus.submission.service.QuizService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
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
@RequestMapping("/internal")
public class InternalQuizController {

    QuizService quizService;

    @PostMapping("/quiz/exercise/{exerciseId}/quiz-detail")
    ApiResponse<QuizDetail> internalAddQuizDetail(
            @PathVariable("exerciseId") String exerciseId,
            @RequestBody @Valid AddQuizDetailRequest addQuizRequest) {

        return ApiResponse.<QuizDetail>builder()
                .message("Tạo Quiz thành công!")
                .result(quizService.addQuizDetail(
                        exerciseId, addQuizRequest, true))
                .build();
    }

    @PostMapping("/quiz/{exerciseId}/question")
    ApiResponse<Question> internalAddQuestion(
            @PathVariable("exerciseId") String exerciseId,
            @RequestBody @Valid QuestionDto questionDto)
            throws BadRequestException {

        return ApiResponse.<Question>builder()
                .message("Thêm câu hỏi cho quiz thành công!")
                .result(quizService.addQuestion(
                        exerciseId, questionDto, true))
                .build();
    }

    @PostMapping("/quiz/question/{questionId}/option")
    ApiResponse<Option> internalAddOption(
            @PathVariable String questionId,
            @RequestBody @Valid OptionDto request) {

        return ApiResponse.<Option>builder()
                .message("Thêm option thành công!")
                .result(quizService.addOption(
                        questionId, request, true))
                .build();
    }
}
