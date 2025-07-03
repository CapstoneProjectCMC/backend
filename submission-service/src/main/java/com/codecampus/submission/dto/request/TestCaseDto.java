package com.codecampus.submission.dto.request;

public record TestCaseDto(
    String input,
    String expectedOutput,
    boolean sample,
    String note)
{
}
