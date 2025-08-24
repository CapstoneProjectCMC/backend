package com.codecampus.ai.dto.request.quiz;

public record OptionDto(
    String optionText,
    Boolean correct,
    String order) {
}
