package com.codecampus.ai.dto.response;

public record StoredFile(
    String publicUrl,
    String originalName,
    String contentType,
    String absolutePath) {
}