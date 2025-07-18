package com.codecampus.submission.dto.response.contest;

import java.time.Instant;

public record MyContestResponse(
        String contestId,
        String title,
        Instant startTime,
        Instant endTime,
        boolean inProgress,
        boolean completed,
        Integer totalScore,
        Integer maxScore,
        Integer totalTimeSeconds,
        int totalQuestions,
        int totalDurationMinutes
) {
}
