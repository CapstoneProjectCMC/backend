package com.codecampus.ai.dto.response;

public record ChatWithImageResponse(
    String aiAnswer,
    StoredFile uploadedFile
) {
}
