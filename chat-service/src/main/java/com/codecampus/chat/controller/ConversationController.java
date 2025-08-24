package com.codecampus.chat.controller;

import com.codecampus.chat.dto.common.ApiResponse;
import com.codecampus.chat.dto.common.PageResponse;
import com.codecampus.chat.dto.request.ConversationRequest;
import com.codecampus.chat.dto.response.ConversationResponse;
import com.codecampus.chat.service.ConversationService;
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
public class ConversationController {

    ConversationService conversationService;

    @PostMapping("/conversation")
    ApiResponse<ConversationResponse> createConversation(
            @RequestBody @Valid ConversationRequest request) {
        return ApiResponse.<ConversationResponse>builder()
                .result(conversationService.createConversation(request))
                .build();
    }

    @GetMapping("/conversations")
    ApiResponse<PageResponse<ConversationResponse>> getMyConversations(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return ApiResponse.<PageResponse<ConversationResponse>>builder()
                .result(conversationService.getMyConversations(page, size))
                .build();
    }
}
