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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    ApiResponse<QuizDetail> addQuizDetail(
            @PathVariable("exerciseId") String exerciseId,
            @RequestBody @Valid AddQuizDetailRequest addQuizRequest) {
        return ApiResponse.<QuizDetail>builder()
                .result(quizService
                        .addQuizDetail(exerciseId, addQuizRequest))
                .message("Tạo Quiz thành công!")
                .build();
    }

    @PostMapping("/quiz/{exerciseId}/question")
    ApiResponse<Question> addQuestion(
            @PathVariable("exerciseId") String exerciseId,
            @RequestBody @Valid QuestionDto questionDto)
            throws BadRequestException {
        return ApiResponse.<Question>builder()
                .result(quizService.addQuestion(exerciseId, questionDto))
                .message("Thêm câu hỏi cho quiz thành công!")
                .build();
    }

    @PostMapping("/quiz/question/{questionId}/option")
    ApiResponse<Option> addOption(
            @PathVariable String questionId,
            @RequestBody @Valid OptionDto request) {

        return ApiResponse.<Option>builder()
                .result(quizService.addOption(questionId, request))
                .message("Thêm option thành công!")
                .build();
    }

    @PatchMapping("/quiz/{exerciseId}/question/{questionId}")
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

    @PatchMapping("/quiz/question/option/{optionId}")
    ApiResponse<Option> updateOption(
            @PathVariable String optionId,
            @RequestBody UpdateOptionRequest request) {
        return ApiResponse.<Option>builder()
                .result(quizService.updateOption(optionId, request))
                .message("Sửa option thành công!")
                .build();
    }

    /* ---------- QUIZ DETAIL ---------- */
    @GetMapping("/quiz/{exerciseId}/detail")
    ApiResponse<QuizDetail> quizDetail(
            @PathVariable String exerciseId) {
        return ApiResponse.<QuizDetail>builder()
                .result(quizService.getQuizDetail(exerciseId))
                .message("Chi tiết quiz!")
                .build();
    }

    /* ---------- QUESTION ---------- */
    @GetMapping("/quiz/{exerciseId}/questions")
    ApiResponse<List<Question>> questionsOfQuiz(
            @PathVariable String exerciseId) {
        return ApiResponse.<List<Question>>builder()
                .result(quizService.getQuestionsOfQuiz(exerciseId))
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
    ApiResponse<List<Option>> optionsOfQuestion(
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
