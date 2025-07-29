// SubmissionResponseDto.java
package com.codecampus.coding.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionResponseDto {
    String submissionId;
    String status; // Accepted, Wrong Answer, Error
    String message;
    List<SubmissionTestCaseResultDto> testCases;
}
