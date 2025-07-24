package com.codecampus.ai.dto.request.exercise;

public record QuizDetailPromptIn(
        CreateExerciseRequest createExerciseRequest,
        int numQuestions
) {
}
