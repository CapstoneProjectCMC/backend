package com.codecampus.chat.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChatMessageRequest(
        @NotBlank
        String conversationId,

        @NotBlank
        String message
) {
}
