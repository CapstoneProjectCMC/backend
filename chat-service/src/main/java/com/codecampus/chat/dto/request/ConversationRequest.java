package com.codecampus.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ConversationRequest(
        String type,

        @Size(min = 1)
        @NotNull
        List<String> participantIds
) {
}
