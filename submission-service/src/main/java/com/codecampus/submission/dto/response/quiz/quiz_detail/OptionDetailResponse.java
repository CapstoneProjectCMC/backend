package com.codecampus.submission.dto.response.quiz.quiz_detail;

import java.time.Instant;
import lombok.Builder;

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
