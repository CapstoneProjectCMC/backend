package com.codecampus.chat.controller;

import com.codecampus.chat.dto.common.ApiResponse;
import com.codecampus.chat.dto.common.PageResponse;
import com.codecampus.chat.dto.request.ChatMessageRequest;
import com.codecampus.chat.dto.response.ChatMessageResponse;
import com.codecampus.chat.service.ChatMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatMessageController {
    ChatMessageService chatMessageService;

    @PostMapping("/message")
    ApiResponse<ChatMessageResponse> createChatMessage(
            @RequestBody @Valid ChatMessageRequest request)
            throws JsonProcessingException {
        return ApiResponse.<ChatMessageResponse>builder()
                .result(chatMessageService.createChatMessage(request))
                .build();
    }

    @GetMapping("/messages")
    ApiResponse<PageResponse<ChatMessageResponse>> getChatMessages(
            @RequestParam("conversationId") String conversationId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return ApiResponse.<PageResponse<ChatMessageResponse>>builder()
                .result(chatMessageService.getChatMessages(
                        conversationId,
                        page, size))
                .build();
    }
}
