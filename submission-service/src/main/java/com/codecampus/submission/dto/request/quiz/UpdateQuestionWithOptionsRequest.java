package com.codecampus.submission.dto.request.quiz;

import com.codecampus.submission.constant.submission.QuestionType;
import java.util.List;

public record UpdateQuestionWithOptionsRequest(
    // Thuộc tính hiện có của Question
    String text,
    QuestionType questionType,
    Integer points,
    Integer orderInQuiz,

    // Options kèm theo
    List<OptionPatchDto> options // có thể rỗng
) {
}
