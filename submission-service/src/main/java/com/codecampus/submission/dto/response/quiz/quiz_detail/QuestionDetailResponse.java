package com.codecampus.submission.dto.response.quiz.quiz_detail;

import java.time.Instant;
import java.util.List;
import lombok.Builder;

@Builder
public record QuestionDetailResponse(
    String id,
    String text,
    int points,
    String type,
    int orderInQuiz,
    List<OptionDetailResponse> options,
    // audit (giữ lại nếu cần hiển thị)
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt,
    String deletedBy,
    Instant deletedAt
) {
}
