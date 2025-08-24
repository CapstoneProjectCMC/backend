package com.codecampus.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExerciseResponse(
    String id,
    String title,
    String description,
    String exerciseType
) {
}
