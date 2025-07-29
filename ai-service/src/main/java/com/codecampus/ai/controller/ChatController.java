package com.codecampus.ai.controller;

import com.codecampus.ai.dto.common.ApiResponse;
import com.codecampus.ai.dto.request.ChatRequest;
import com.codecampus.ai.service.ChatService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Builder
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatController {
    ChatService chatService;

    @PostMapping("/chat")
    ApiResponse<String> chat(
            @RequestBody ChatRequest request) {
        return ApiResponse.<String>builder()
                .message("Kết quả chat với AI!")
                .result(chatService.chat(request))
                .build();
    }

    @PostMapping("/chat-with-image")
    ApiResponse<String> chatWithImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("message") String message) {
        return ApiResponse.<String>builder()
                .message("Kết quả chat với AI!")
                .result(chatService.chatWithImage(file, message))
                .build();
    }
}
