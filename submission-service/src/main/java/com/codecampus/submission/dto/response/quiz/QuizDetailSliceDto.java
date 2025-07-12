package com.codecampus.submission.dto.response.quiz;

import java.time.Instant;
import java.util.List;

public record QuizDetailSliceDto(
        String id,
        int numQuestions,
        int totalPoints,

        /* thông tin phân trang câu hỏi */
        int currentPage,
        int totalPages,
        int pageSize,
        long totalElements,

        List<QuestionBriefDto> questions,
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt,
        String deletedBy,
        Instant deletedAt) {
}