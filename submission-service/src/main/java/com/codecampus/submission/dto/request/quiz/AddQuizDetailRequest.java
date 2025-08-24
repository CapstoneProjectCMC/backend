package com.codecampus.submission.dto.request.quiz;

import java.util.List;

public record AddQuizDetailRequest(
    List<QuestionDto> questions) {
}
