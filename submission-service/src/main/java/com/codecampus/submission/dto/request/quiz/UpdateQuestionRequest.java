package com.codecampus.submission.dto.request.quiz;

import com.codecampus.submission.constant.submission.QuestionType;

public record UpdateQuestionRequest(
        String text,
        QuestionType questionType,
        Integer points,
        Integer orderInQuiz
) {
}
