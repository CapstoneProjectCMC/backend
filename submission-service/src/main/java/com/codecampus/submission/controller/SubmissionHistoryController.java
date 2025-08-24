package com.codecampus.submission.controller;

import com.codecampus.submission.dto.common.ApiResponse;
import com.codecampus.submission.dto.common.PageResponse;
import com.codecampus.submission.dto.response.AllSubmissionHistoryResponse;
import com.codecampus.submission.dto.response.coding.CodingAttemptHistoryResponse;
import com.codecampus.submission.dto.response.quiz.QuizAttemptHistoryResponse;
import com.codecampus.submission.service.SubmissionHistoryService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubmissionHistoryController {
  SubmissionHistoryService submissionHistoryService;

  @GetMapping("/quiz/self/history")
  ApiResponse<PageResponse<QuizAttemptHistoryResponse>> myQuizHistory(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<QuizAttemptHistoryResponse>>builder()
        .result(submissionHistoryService
            .getQuizAttemptHistoriesForStudent(page, size))
        .message("Lịch sử làm bài quiz của bạn!")
        .build();
  }

  @GetMapping("/coding/self/history")
  ApiResponse<PageResponse<CodingAttemptHistoryResponse>> myCodingHistory(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<CodingAttemptHistoryResponse>>builder()
        .result(submissionHistoryService
            .getCodingAttemptHistoriesForStudent(page, size))
        .message("Lịch sử làm bài code của bạn!")
        .build();
  }

  @GetMapping("/self/history")
  ApiResponse<PageResponse<AllSubmissionHistoryResponse>> mySubmissions(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.<PageResponse<AllSubmissionHistoryResponse>>builder()
        .result(submissionHistoryService.mySubmissions(page, size))
        .message("Tất cả bài nộp của bạn")
        .build();
  }
}
