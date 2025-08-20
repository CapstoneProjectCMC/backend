package com.codecampus.search.dto.response;

import com.codecampus.search.constant.submission.Difficulty;
import com.codecampus.search.constant.submission.ExerciseType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Builder
public record ExerciseSearchResponse(
        String id,
        String userId,
        String title,
        String description,
        Difficulty difficulty,
        ExerciseType exerciseType,
        String orgId,
        BigDecimal cost,
        boolean freeForOrg,
        Set<String> tags,
        boolean visibility,
        Instant createdAt
) {
}
