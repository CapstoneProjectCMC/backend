package com.codecampus.submission.dto.data;

import java.time.Instant;

public record ContestSummaryDto(
    String id,
    String title,
    Instant startTime,
    Instant endTime,
    boolean rankPublic
) {
}