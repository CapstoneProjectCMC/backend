package com.codecampus.ai.dto.response.chat;

import java.time.Instant;
import java.util.List;

public record ThreadDetailResponse(
        String id,
        String title,
        Instant lastMessageAt,
        Instant createdAt,
        Instant updatedAt,
        List<MessageResponse> messages
) {
}