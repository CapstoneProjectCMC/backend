package com.codecampus.ai.dto.request.quiz;

import com.codecampus.ai.constant.exercise.Difficulty;
import com.codecampus.quiz.grpc.QuestionType;
import java.util.Set;

public record GenerateQuestionsPromptIn(
    String exerciseId,
    String title,
    String description,
    Difficulty difficulty,
    Integer duration,
    Set<String> tags,

    QuestionType questionType,  // SINGLE_CHOICE | MULTI_CHOICE | FILL_BLANK
    Integer points,
    Integer numQuestions
) {
}