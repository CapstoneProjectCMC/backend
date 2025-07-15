package com.codecampus.submission.dto.response.quiz;

import lombok.Builder;

import java.time.Instant;

@Builder
// Mặc định cho phép response cả null khi Dev
// Khi build thì KHÔNG response null
// @JsonInclude(JsonInclude.Include.NON_NULL)
public record QuestionBriefDto(
        String id,
        String text,
        int points,
        String type,
        int orderInQuiz,
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt,
        String deletedBy,
        Instant deletedAt) {
}