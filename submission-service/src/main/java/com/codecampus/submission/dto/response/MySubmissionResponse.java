package com.codecampus.submission.dto.response;

import java.time.Instant;

public record MySubmissionResponse(
        String submissionId,
        String exerciseId,
        String exerciseTitle,
        Integer score,          // null nếu code‑submission chưa chấm
        Integer totalPoints,    // quiz.totalPoints | coding.sốTestCase
        Integer timeTakenSecs,  // quiz | coding
        Instant submittedAt,
        String status           // PASSED | FAILED | …
) {
}