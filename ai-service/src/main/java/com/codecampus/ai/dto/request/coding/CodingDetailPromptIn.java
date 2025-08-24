package com.codecampus.ai.dto.request.coding;

import com.codecampus.ai.dto.request.exercise.CreateExerciseRequest;

import java.util.Set;

public record CodingDetailPromptIn(
        CreateExerciseRequest createExerciseRequest,
        Integer numTestCases,
        Set<String> preferredLanguages,
        Integer timeLimitMs,
        Integer memoryLimitMb,
        Integer maxSubmissions
) {
}