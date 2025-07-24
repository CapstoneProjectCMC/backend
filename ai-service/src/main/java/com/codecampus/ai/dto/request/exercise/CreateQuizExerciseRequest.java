package com.codecampus.ai.dto.request.exercise;

public record CreateQuizExerciseRequest(
        CreateExerciseRequest exerciseRequest,
        AddQuizDetailRequest quizDetailRequest
) {
}