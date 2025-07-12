package com.codecampus.submission.dto.response.quiz;

import com.codecampus.submission.constant.submission.ExerciseType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Builder
public record ExerciseDetailQuizDto(
        String id,
        String userId,
        String title,
        String description,
        ExerciseType exerciseType,
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