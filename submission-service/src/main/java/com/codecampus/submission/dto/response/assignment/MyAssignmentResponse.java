package com.codecampus.submission.dto.response.assignment;

import com.codecampus.submission.constant.submission.ExerciseType;

import java.time.Instant;

public record MyAssignmentResponse(
        String assignmentId,
        String exerciseId,
        String exerciseTitle,
        Instant dueAt,
        boolean completed,
        Instant completedAt,
        Integer myBestScore,
        Integer totalPoints,
        ExerciseType exerciseType
) {
}
