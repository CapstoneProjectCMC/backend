package com.codecampus.submission.dto.response.quiz.quiz_detail;

import com.codecampus.submission.constant.submission.Difficulty;
import com.codecampus.submission.constant.submission.ExerciseType;
import dtos.UserSummary;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import lombok.Builder;

@Builder
public record ExerciseQuizDetailResponse(
    String id,
    UserSummary user,
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
    boolean allowAiQuestion,
    boolean visibility,
    Boolean purchased,
    QuizDetailSliceDetailResponse quizDetail,
    // audit
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt,
    String deletedBy,
    Instant deletedAt
) {
}
