package com.codecampus.submission.dto.request;

public record OptionDto(
    String optionText,
    boolean correct,
    String order)
{
}
