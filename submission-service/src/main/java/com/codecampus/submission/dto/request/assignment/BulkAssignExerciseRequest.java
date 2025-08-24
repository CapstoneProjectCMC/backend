package com.codecampus.submission.dto.request.assignment;

import jakarta.validation.constraints.NotEmpty;
import java.time.Instant;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;

public record BulkAssignExerciseRequest(
    @NotEmpty Set<String> studentIds,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    Instant dueAt // cho phép null
) {
}
