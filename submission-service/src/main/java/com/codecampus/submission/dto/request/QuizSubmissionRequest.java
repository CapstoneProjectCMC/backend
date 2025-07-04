package com.codecampus.submission.dto.request;

import java.util.List;

public record QuizSubmissionRequest(
    String exerciseId,
    List<AnswerDto> answers)
{
}
