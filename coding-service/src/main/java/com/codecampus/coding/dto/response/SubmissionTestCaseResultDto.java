package com.codecampus.coding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionTestCaseResultDto {
    String input;
    String expectedOutput;
    String actualOutput;
    boolean passed;
    long executionTimeMs;
}
