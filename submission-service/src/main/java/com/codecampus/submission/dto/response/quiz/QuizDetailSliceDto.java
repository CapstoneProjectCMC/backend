package com.codecampus.submission.dto.response.quiz;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
// Mặc định cho phép response cả null khi Dev
// Khi build thì KHÔNG response null
// @JsonInclude(JsonInclude.Include.NON_NULL)
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