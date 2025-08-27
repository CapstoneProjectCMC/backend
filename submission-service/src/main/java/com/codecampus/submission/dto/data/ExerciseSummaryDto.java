package com.codecampus.submission.dto.data;

import com.codecampus.submission.constant.submission.Difficulty;
import com.codecampus.submission.constant.submission.ExerciseType;

public record ExerciseSummaryDto(
    String id,
    String title,
    ExerciseType exerciseType,
    Difficulty difficulty,
    boolean visibility,
    String orgId,
    boolean created,
    boolean completed
) {
}
