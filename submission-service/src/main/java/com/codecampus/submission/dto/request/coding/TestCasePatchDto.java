package com.codecampus.submission.dto.request.coding;

public record TestCasePatchDto(
    String id,
    String input,
    String expectedOutput,
    Boolean sample,
    String note,
    Boolean delete
) {
}