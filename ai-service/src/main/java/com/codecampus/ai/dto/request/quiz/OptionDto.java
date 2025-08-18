package com.codecampus.ai.dto.request.quiz;

public record OptionDto(
        String optionText,
        boolean correct,
        String order) {
}
