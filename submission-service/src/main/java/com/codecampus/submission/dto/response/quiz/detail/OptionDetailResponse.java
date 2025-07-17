package com.codecampus.submission.dto.response.quiz.detail;

import lombok.Builder;

import java.time.Instant;

@Builder
public record OptionDetailResponse(
        String id,
        String optionText,
        boolean correct,
        String order,
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt,
        String deletedBy,
        Instant deletedAt
) {
}
