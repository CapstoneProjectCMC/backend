package com.codecampus.ai.dto.request.exercise;

import com.codecampus.ai.constant.exercise.Difficulty;

import java.util.Set;

public record ExercisePromptIn(
        String title,
        String description,
        Difficulty difficulty,
        int duration,
        Set<String> tags
) {
}
