package com.codecampus.ai.dto.request.quiz;

import com.codecampus.ai.dto.request.exercise.ExercisePromptIn;

public record GenerateQuizPromptIn(
        ExercisePromptIn exercisePromptIn,
        int numQuestions) {
}
