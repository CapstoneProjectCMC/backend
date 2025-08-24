package com.codecampus.submission.dto.request.quiz;

import com.codecampus.submission.constant.submission.QuestionType;
import java.util.List;

public record QuestionDto(
    String text,
    QuestionType questionType,
    int points,
    int orderInQuiz,
    List<OptionDto> options) {
}