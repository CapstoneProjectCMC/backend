// SubmissionResponseDto.java
package com.codecampus.coding.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public record SubmissionResponseDto(
        String submissionId,
        int score,
        int totalPoints,
        boolean passed,
        List<SubmissionTestCaseResultDto> testCases) {
}
