package com.codecampus.search.dto.response;

import com.codecampus.search.constant.submission.Difficulty;
import com.codecampus.search.constant.submission.ExerciseType;
import dtos.UserSummary;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Builder(toBuilder = true)
public record ExerciseSearchResponse(
        String id,
        UserSummary user,
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
