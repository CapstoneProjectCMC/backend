package com.codecampus.ai.controller;

import com.codecampus.ai.dto.request.BillItem;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class ChatController {
    ChatService chatService;

    @PostMapping("/chat")
    String chat(@RequestBody ChatRequest request) {
        return chatService.chat(request);
    }

    @PostMapping("/chat-with-image")
    List<BillItem> chatWithImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("message") String message) {
        return chatService.chatWithImage(file, message);
    }
}
