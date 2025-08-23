package com.codecampus.ai.dto.request.quiz;

import com.codecampus.ai.dto.request.exercise.CreateExerciseRequest;

public record QuizDetailPromptIn(
        CreateExerciseRequest createExerciseRequest,
        Integer numQuestions
) {
}
