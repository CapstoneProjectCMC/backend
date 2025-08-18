package com.codecampus.ai.dto.request.quiz;

import com.codecampus.quiz.grpc.QuestionType;

import java.util.List;

public record QuestionDto(
        String text,
        QuestionType questionType,
        int points,
        int orderInQuiz,
        List<OptionDto> options) {
}