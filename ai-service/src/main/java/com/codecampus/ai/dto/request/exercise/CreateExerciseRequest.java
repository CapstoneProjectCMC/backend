package com.codecampus.ai.dto.request.exercise;

import com.codecampus.ai.constant.exercise.Difficulty;
import com.codecampus.ai.constant.exercise.ExerciseType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record CreateExerciseRequest(
        String title,
        String description,
        Difficulty difficulty,
        ExerciseType exerciseType,
        String orgId, // Có trong tổ chức hay không
        BigDecimal cost,
        Boolean freeForOrg,
        Instant startTime,
        Instant endTime,
        int duration,
        String allowDiscussionId, //Cho thảo luận ở forum nào không
        Set<String> resourceIds, //Thuộc tài liệu nào
        Set<String> tags,
        Boolean allowAiQuestion
) {
}
