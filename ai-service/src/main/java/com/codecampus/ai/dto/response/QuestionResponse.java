package com.codecampus.ai.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QuestionResponse(
    String id,
    String text,
    Integer points,
    List<OptionResponse> options
) {
}
