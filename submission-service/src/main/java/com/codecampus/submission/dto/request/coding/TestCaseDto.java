package com.codecampus.submission.dto.request.coding;

public record TestCaseDto(
    String input,
    String expectedOutput,
    boolean sample,
    String note) {
}
