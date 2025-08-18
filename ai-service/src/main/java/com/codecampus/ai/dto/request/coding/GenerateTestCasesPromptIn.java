package com.codecampus.ai.dto.request.coding;

public record GenerateTestCasesPromptIn(
        String exerciseId,
        String title,
        String description,
        String input,
        String output,
        String constraintText,
        Integer timeLimit,
        Integer memoryLimit,
        Integer numTestCases
) {
}