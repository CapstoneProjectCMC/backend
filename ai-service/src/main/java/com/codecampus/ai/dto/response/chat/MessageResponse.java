package com.codecampus.ai.dto.response.chat;

import java.time.Instant;

public record MessageResponse(
    String id,
    String role,           // USER | ASSISTANT | SYSTEM
    String content,
    String imageOriginalName,
    String imageContentType,
    String imageUrl,
    Instant createdAt
) {
}