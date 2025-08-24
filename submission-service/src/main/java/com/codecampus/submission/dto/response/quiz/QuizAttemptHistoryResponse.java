package com.codecampus.submission.dto.response.quiz;

import java.time.Instant;

public record QuizAttemptHistoryResponse(
        String submissionId,
        String exerciseId,
        String exerciseTitle,
        Integer score,
        Integer totalPoints,
        Integer timeTakenSeconds,
        Instant submittedAt,
        Boolean passed
) {
}
