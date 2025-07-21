package com.codecampus.ai.dto.request.exercise;

import com.codecampus.quiz.grpc.QuestionType;

import java.util.List;

public record QuestionGenDto(
        String text,
        QuestionType questionType,
        int points,
        int orderInQuiz,
        List<OptionGenDto> options
) {
}
