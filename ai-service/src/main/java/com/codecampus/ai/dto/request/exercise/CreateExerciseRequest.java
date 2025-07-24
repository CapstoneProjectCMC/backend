package com.codecampus.ai.dto.request.exercise;

import com.codecampus.ai.constant.exercise.Difficulty;
import com.codecampus.ai.constant.exercise.ExerciseType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

public record CreateExerciseRequest(
        String title, //Cần cho input AI
        String description, //Cần cho input AI
        Difficulty difficulty, //Cần cho input AI
        ExerciseType exerciseType,
        String orgId,
        BigDecimal cost,
        Boolean freeForOrg,
        Instant startTime,
        Instant endTime,
        int duration, //Cần cho input AI
        String allowDiscussionId,
        Set<String> resourceIds,
        Set<String> tags, //Cần cho input AI
        Boolean allowAiQuestion
) {
}
