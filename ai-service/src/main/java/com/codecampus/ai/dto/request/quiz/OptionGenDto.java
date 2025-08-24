package com.codecampus.ai.dto.request.quiz;

public record OptionGenDto(
    String optionText,
    Boolean correct,
    String order
) {
}
