package com.codecampus.submission.dto.response;

import com.codecampus.submission.constant.submission.ExerciseType;

import java.time.Instant;

public record AllSubmissionHistoryResponse(
        String submissionId,
        String exerciseId,
        String exerciseTitle,
        ExerciseType exerciseType,
        Integer score,
        Integer totalPoints,
        Integer timeTakenSeconds,
        Instant submittedAt,
        Boolean passed
) {
}
