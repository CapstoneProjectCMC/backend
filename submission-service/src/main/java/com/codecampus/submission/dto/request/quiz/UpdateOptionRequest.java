package com.codecampus.submission.dto.request.quiz;

public record UpdateOptionRequest(
        String optionText,
        Boolean correct,
        String order
) {
}
