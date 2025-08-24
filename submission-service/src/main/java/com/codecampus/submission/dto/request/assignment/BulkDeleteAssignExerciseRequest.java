package com.codecampus.submission.dto.request.assignment;

import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record BulkDeleteAssignExerciseRequest(
    @NotEmpty Set<String> studentIds
) {
}
