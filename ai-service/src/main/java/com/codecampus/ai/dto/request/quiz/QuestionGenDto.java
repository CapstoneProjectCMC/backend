package com.codecampus.ai.dto.request.quiz;

import com.codecampus.quiz.grpc.QuestionType;

import java.util.List;

public record QuestionGenDto(
        String text,
        QuestionType questionType, //Cần cho input AI
        int points, //Cần cho input AI
        int orderInQuiz,
        List<OptionGenDto> options
) {
}
