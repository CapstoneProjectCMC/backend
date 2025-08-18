package com.codecampus.ai.dto.request.quiz;

import java.util.List;

public record AddQuizDetailRequest(
        List<QuestionDto> questions) {
}
