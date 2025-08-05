package com.codecampus.coding.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public record SubmissionTestCaseResultDto(
        String input,
        String expectedOutput,
        String actualOutput,
        boolean passed,
        long executionTimeMs) {
}
