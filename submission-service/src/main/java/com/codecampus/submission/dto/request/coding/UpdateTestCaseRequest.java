package com.codecampus.submission.dto.request.coding;

public record UpdateTestCaseRequest(
    String input,
    String expectedOutput,
    Boolean sample,
    String note) {
}