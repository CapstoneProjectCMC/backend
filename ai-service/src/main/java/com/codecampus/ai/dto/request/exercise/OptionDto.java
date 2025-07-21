package com.codecampus.ai.dto.request.exercise;

public record OptionDto(
        String optionText,
        boolean correct,
        String order) {
}
