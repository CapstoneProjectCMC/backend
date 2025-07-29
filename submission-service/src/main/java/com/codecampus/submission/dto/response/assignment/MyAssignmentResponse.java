package com.codecampus.submission.dto.response.assignment;

import java.time.Instant;

public record MyAssignmentResponse(
        String assignmentId,
        String exerciseId,
        String exerciseTitle,
        Instant dueAt,
        boolean completed,
        Integer myBestScore,
        Integer totalPoints
) {
}
