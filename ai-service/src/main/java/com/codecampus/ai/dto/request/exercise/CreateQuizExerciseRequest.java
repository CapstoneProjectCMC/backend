package com.codecampus.ai.dto.request.exercise;

public record CreateQuizExerciseRequest(
        CreateExerciseRequest createExerciseRequest,
        AddQuizDetailRequest addQuizDetailRequest
) {
}