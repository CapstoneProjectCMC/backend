package com.codecampus.ai.dto.request.quiz;

public record OptionGenDto(
        String optionText,
        boolean correct,
        String order
) {
}
