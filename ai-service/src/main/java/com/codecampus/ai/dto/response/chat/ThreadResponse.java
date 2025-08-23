package com.codecampus.ai.dto.response.chat;

import java.time.Instant;

public record ThreadResponse(
        String id,
        String title,
        Instant lastMessageAt,
        Instant createdAt,
        Instant updatedAt
) {
}
