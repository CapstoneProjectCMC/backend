package com.codecampus.submission.controller;

import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.response.quiz.QuizAttemptHistoryResponse;
import com.codecampus.submission.service.SubmissionHistoryService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubmissionHistoryController {
    SubmissionHistoryService submissionHistoryService;

    @GetMapping("/submission/quiz/self/history")
    ApiResponse<List<QuizAttemptHistoryResponse>> myQuizHistory() {
        return ApiResponse.<List<QuizAttemptHistoryResponse>>builder()
                .result(submissionHistoryService.getQuizAttemptHistoriesForStudent())
                .message("Lịch sử làm bài quiz của bạn!")
                .build();
    }
}
