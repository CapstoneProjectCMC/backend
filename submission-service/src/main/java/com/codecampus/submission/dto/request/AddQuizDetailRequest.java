package com.codecampus.submission.dto.request;

import java.util.List;

public record AddQuizDetailRequest(
    List<QuestionDto> questions)
{
}
