package com.codecampus.coding.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public record SubmissionRequestDto(
        String submissionId,
        String exerciseId,
        String studentId,
        String language,
        String sourceCode,
        int memoryMb,
        float cpus,
        int timeTakenSeconds) {
}
