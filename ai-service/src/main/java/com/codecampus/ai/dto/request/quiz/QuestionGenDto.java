package com.codecampus.ai.dto.request.quiz;

import com.codecampus.quiz.grpc.QuestionType;
import java.util.List;

public record QuestionGenDto(
    String text,
    QuestionType questionType, //Cần cho input AI
    Integer points, //Cần cho input AI
    Integer orderInQuiz,
    List<OptionGenDto> options
) {
}
