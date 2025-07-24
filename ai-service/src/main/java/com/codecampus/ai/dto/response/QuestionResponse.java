package com.codecampus.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestionResponse(
        String id,
        String text,
        int points
) {
}
