package com.codecampus.ai.dto.request.exercise;

import com.codecampus.ai.constant.exercise.Difficulty;
import com.codecampus.ai.constant.exercise.ExerciseType;

import java.math.BigDecimal;

public record ExerciseGenDto(
        String title,
        String description,
        Difficulty difficulty,
        ExerciseType exerciseType,
        BigDecimal cost,
        int duration
) {
}
