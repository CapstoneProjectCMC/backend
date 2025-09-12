package com.codecampus.submission.dto.response.stats;

import com.codecampus.submission.constant.submission.ExerciseType;
import java.time.Instant;
import lombok.Builder;

@Builder
public record ExerciseStatRow(
    String exerciseId,
    String title,
    ExerciseType exerciseType,
    Boolean visibility,
    String orgId,

    // assignments
    long assignedCount,
    long completedCount,
    Double completionRate,   // completed/assigned

    // submissions
    long submissionCount,
    long passedCount,
    Double passRate,         // passed/submissions
    Double avgScore,         // AVG(score) từ submission
    Instant lastSubmissionAt
) {
}