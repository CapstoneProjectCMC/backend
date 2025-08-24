package com.codecampus.submission.dto.response.coding.coding_detail;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import lombok.Builder;

@Builder
public record CodingDetailSliceDetailResponse(
    String id,
    String topic,
    Set<String> allowedLanguages,
    String input,
    String output,
    String constraintText,
    Integer timeLimit,
    Integer memoryLimit,
    Integer maxSubmissions,
    String codeTemplate,
    String solution,

    int currentPage,
    int totalPages,
    int pageSize,
    long totalElements,

    List<TestCaseDetailResponse> testCases,

    // audit
    String createdBy, Instant createdAt,
    String updatedBy, Instant updatedAt,
    String deletedBy, Instant deletedAt
) {
}
