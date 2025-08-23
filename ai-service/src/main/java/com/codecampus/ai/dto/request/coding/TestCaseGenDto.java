package com.codecampus.ai.dto.request.coding;

public record TestCaseGenDto(
        String input,
        String expectedOutput,
        Boolean sample,
        String note
) {
}