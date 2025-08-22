package com.codecampus.ai.dto.request.coding;

public record TestCaseDto(
        String input,
        String expectedOutput,
        Boolean sample,
        String note
) {
}