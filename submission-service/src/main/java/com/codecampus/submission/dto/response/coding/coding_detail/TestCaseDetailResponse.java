package com.codecampus.submission.dto.response.coding.coding_detail;

import lombok.Builder;

import java.time.Instant;

@Builder
public record TestCaseDetailResponse(
        String id,
        String input,
        String expectedOutput,
        boolean sample,
        String note,
        // audit
        String createdBy, Instant createdAt,
        String updatedBy, Instant updatedAt,
        String deletedBy, Instant deletedAt
) {
}
