package com.codecampus.submission.dto.response.quiz;

import com.codecampus.submission.constant.submission.Difficulty;
import com.codecampus.submission.constant.submission.ExerciseType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Builder
// Mặc định cho phép response cả null khi Dev
// Khi build thì KHÔNG response null
// @JsonInclude(JsonInclude.Include.NON_NULL)
public record ExerciseQuizResponse(
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
        Instant createdAt) {
}