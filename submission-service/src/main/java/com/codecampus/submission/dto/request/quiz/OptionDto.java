package com.codecampus.submission.dto.request.quiz;

public record OptionDto(
        String optionText,
        boolean correct,
        String order) {
}
