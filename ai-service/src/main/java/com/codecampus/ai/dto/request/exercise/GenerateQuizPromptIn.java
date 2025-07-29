package com.codecampus.ai.dto.request.exercise;

public record GenerateQuizPromptIn(
        ExercisePromptIn exercisePromptIn,
        int numQuestions) {
}
