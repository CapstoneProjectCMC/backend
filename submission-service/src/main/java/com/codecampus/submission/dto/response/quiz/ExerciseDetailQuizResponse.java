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
public record ExerciseDetailQuizResponse(
        String id,
        String userId,
        String title,
        String description,
        ExerciseType exerciseType,
        Difficulty difficulty,
        String orgId,
        boolean active,
        BigDecimal cost,
        boolean freeForOrg,
        Instant startTime,
        Instant endTime,
        int duration,
        String allowDiscussionId,
        Set<String> resourceIds,
        Set<String> tags,
        QuizDetailSliceDto quizDetail,
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt,
        String deletedBy,
        Instant deletedAt) {
}