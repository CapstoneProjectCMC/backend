package com.codecampus.submission.dto.response.quiz.quiz_detail;

import java.time.Instant;
import java.util.List;
import lombok.Builder;

@Builder
public record QuizDetailSliceDetailResponse(
    String id,
    int numQuestions,
    int totalPoints,

    /* thông tin phân trang câu hỏi */
    int currentPage,
    int totalPages,
    int pageSize,
    long totalElements,

    List<QuestionDetailResponse> questions,

    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt,
    String deletedBy,
    Instant deletedAt
) {
}
