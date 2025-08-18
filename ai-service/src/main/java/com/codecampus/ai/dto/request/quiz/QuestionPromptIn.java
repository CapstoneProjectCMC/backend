package com.codecampus.ai.dto.request.quiz;

import com.codecampus.ai.constant.exercise.Difficulty;
import com.codecampus.quiz.grpc.QuestionType;

import java.util.Set;

public record QuestionPromptIn(
        String exerciseId,
        String title,
        String description,
        Difficulty difficulty,
        int duration,
        Set<String> tags,
        QuestionType questionType,
        int points
) {
}
