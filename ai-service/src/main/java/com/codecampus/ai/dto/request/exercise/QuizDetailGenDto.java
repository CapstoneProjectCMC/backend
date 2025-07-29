package com.codecampus.ai.dto.request.exercise;

import java.util.List;

public record QuizDetailGenDto(
        List<QuestionGenDto> questions
) {
}
