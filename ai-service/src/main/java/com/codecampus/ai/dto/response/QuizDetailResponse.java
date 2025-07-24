package com.codecampus.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QuizDetailResponse(
        String id,
        int numQuestions,
        int totalPoints,
        List<QuestionResponse> questions
) {
}