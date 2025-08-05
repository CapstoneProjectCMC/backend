package com.codecampus.coding.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public record SubmissionRequestDto(
        String submissionId,
        String submittedCode,
        String userId,
        String exerciseId,
        int memory,
        float cpus) {
}
