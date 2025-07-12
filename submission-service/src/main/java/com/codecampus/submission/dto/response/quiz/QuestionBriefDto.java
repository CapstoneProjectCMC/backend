package com.codecampus.submission.dto.response.quiz;

import java.time.Instant;

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