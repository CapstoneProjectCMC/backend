package com.codecampus.ai.dto.request.exercise;

public record OptionGenDto(
        String optionText,
        boolean correct,
        String order
) {
}
