package com.codecampus.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OptionResponse(
    String id,
    String optionText,
    Boolean correct,
    String order
) {
}
