package com.codecampus.ai.dto.request.coding;

import com.codecampus.ai.dto.request.exercise.ExercisePromptIn;

import java.util.Set;

public record GenerateCodingPromptIn(
        ExercisePromptIn exercisePromptIn,
        Set<String> allowedLanguages,
        Integer timeLimit,
        Integer memoryLimit,
        Integer maxSubmissions,
        Integer numTestCases
) {
}