package com.codecampus.submission.dto.response.assignment;

import java.time.Instant;
import lombok.Builder;

@Builder
public record BulkAssignExerciseResponse(
    String exerciseId,
    long studentsAssigned,
    Instant dueAt
) {
}
