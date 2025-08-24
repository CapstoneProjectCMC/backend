package com.codecampus.submission.dto.request.quiz;

public record OptionPatchDto(
    String id, // null  ⇒ thêm mới
    String optionText,
    Boolean correct,
    String order,
    Boolean delete // true  ⇒ xoá
) {
}
