package com.codecampus.submission.controller;

import com.codecampus.submission.constant.sort.SortField;
import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.common.PageResponse;
import com.codecampus.submission.dto.request.quiz.AddQuizDetailRequest;
import com.codecampus.submission.dto.request.quiz.OptionDto;
import com.codecampus.submission.dto.request.quiz.QuestionDto;
import com.codecampus.submission.dto.request.quiz.UpdateOptionRequest;
import com.codecampus.submission.dto.request.quiz.UpdateQuestionRequest;
import com.codecampus.submission.dto.response.quiz.QuizDetailSliceDto;
import com.codecampus.submission.entity.Option;
import com.codecampus.submission.entity.Question;
import com.codecampus.submission.service.ExerciseService;
import com.codecampus.submission.service.QuizService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizController {
    ExerciseService exerciseService;
    QuizService quizService;


    @PostMapping("/quiz/exercise/{exerciseId}/quiz-detail")
    ApiResponse<Void> addQuizDetail(
            @PathVariable("exerciseId") String exerciseId,
            @RequestBody @Valid AddQuizDetailRequest addQuizRequest) {

        quizService.addQuizDetail(exerciseId, addQuizRequest);

        return ApiResponse.<Void>builder()
                .message("Tạo Quiz thành công!")
                .build();
    }

    @PostMapping("/quiz/{exerciseId}/question")
    ApiResponse<Void> addQuestion(
            @PathVariable("exerciseId") String exerciseId,
            @RequestBody @Valid QuestionDto questionDto)
            throws BadRequestException {

        quizService.addQuestion(exerciseId, questionDto);

        return ApiResponse.<Void>builder()
                .message("Thêm câu hỏi cho quiz thành công!")
                .build();
    }

    @PostMapping("/quiz/question/{questionId}/option")
    ApiResponse<Void> addOption(
            @PathVariable String questionId,
            @RequestBody @Valid OptionDto request) {

        quizService.addOption(questionId, request);

        return ApiResponse.<Void>builder()
                .message("Thêm option thành công!")
                .build();
    }

    @PatchMapping("/quiz/{exerciseId}/question/{questionId}")
    ApiResponse<Void> updateQuestion(
            @PathVariable String exerciseId,
            @PathVariable String questionId,
            @RequestBody UpdateQuestionRequest request) {

        quizService.updateQuestion(exerciseId, questionId, request);

        return ApiResponse.<Void>builder()
                .message("Sửa question thành công!")
                .build();
    }

    @PatchMapping("/quiz/question/option/{optionId}")
    ApiResponse<Void> updateOption(
            @PathVariable String optionId,
            @RequestBody UpdateOptionRequest request) {

        quizService.updateOption(optionId, request);

        return ApiResponse.<Void>builder()
                .message("Sửa option thành công!")
                .build();
    }

    /* ---------- QUIZ DETAIL ---------- */
    @GetMapping("/quiz/{exerciseId}/detail")
    ApiResponse<QuizDetailSliceDto> getQuizDetail(
            @PathVariable String exerciseId,
            @RequestParam(defaultValue = "1") int qPage,
            @RequestParam(defaultValue = "5") int qSize,
            @RequestParam(defaultValue = "ORDER_IN_QUIZ") SortField qSortBy,
            @RequestParam(defaultValue = "false") boolean qAsc) {
        return ApiResponse.<QuizDetailSliceDto>builder()
                .result(quizService.getQuizDetail(
                        exerciseId,
                        qPage, qSize,
                        qSortBy, qAsc)
                )
                .message("Chi tiết quiz!")
                .build();
    }

    /* ---------- QUESTION ---------- */
    @GetMapping("/quiz/{exerciseId}/questions")
    ApiResponse<PageResponse<Question>> getQuestionsOfQuiz(
            @PathVariable String exerciseId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ORDER_IN_QUIZ") SortField sortBy,
            @RequestParam(defaultValue = "false") boolean asc) {
        return ApiResponse.<PageResponse<Question>>builder()
                .result(quizService.getQuestionsOfQuiz(
                        exerciseId,
                        page, size,
                        sortBy, asc
                ))
                .message("Danh sách câu hỏi!")
                .build();
    }

    @GetMapping("/question/{questionId}")
    ApiResponse<Question> getQuestion(@PathVariable String questionId) {
        return ApiResponse.<Question>builder()
                .result(quizService.getQuestion(questionId))
                .message("Chi tiết question")
                .build();
    }

    /* ---------- OPTION ---------- */
    @GetMapping("/question/{questionId}/option")
    ApiResponse<List<Option>> getOptionsOfQuestion(
            @PathVariable String questionId) {
        return ApiResponse.<List<Option>>builder()
                .result(quizService.getOptionsOfQuestion(questionId))
                .message("Danh sách option")
                .build();
    }

    @GetMapping("/option/{optionId}")
    ApiResponse<Option> getOption(@PathVariable String optionId) {
        return ApiResponse.<Option>builder()
                .result(quizService.getOption(optionId))
                .message("Chi tiết option")
                .build();
    }
}
