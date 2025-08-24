package com.codecampus.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TestCaseResponse(
        String id,
        String input,
        String expectedOutput,
        Boolean sample,
        String note
) {
}