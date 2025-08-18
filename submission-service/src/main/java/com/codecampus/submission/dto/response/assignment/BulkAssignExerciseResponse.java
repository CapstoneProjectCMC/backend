package com.codecampus.submission.dto.response.assignment;

import lombok.Builder;

import java.time.Instant;

@Builder
public record BulkAssignExerciseResponse(
        String exerciseId,
        long studentsAssigned,
        Instant dueAt
) {
}
