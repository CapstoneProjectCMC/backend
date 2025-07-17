package com.codecampus.submission.dto.response.quiz.detail;

import com.codecampus.submission.constant.submission.Difficulty;
import com.codecampus.submission.constant.submission.ExerciseType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Builder
public record ExerciseQuizDetailResponse(
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
        boolean allowAiQuestion,                      // <<< MỚI
        QuizDetailSliceDetailResponse quizDetail,     // <<< Question + Option
        // audit
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt,
        String deletedBy,
        Instant deletedAt
) {
}
