package com.codecampus.ai.dto.request.exercise;

public record QuizDraft(
        CreateExerciseRequest exerciseRequest,
        AddQuizDetailRequest quizDetailRequest
) {
}