package com.codecampus.ai.controller;

import com.codecampus.ai.dto.request.ChatRequest;
import com.codecampus.ai.service.ChatService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Builder
@Slf4j
public class ChatController
{
  ChatService chatService;

  @PostMapping("/chat")
  String chat(@RequestBody ChatRequest request)
  {
    return chatService.chat(request);
  }
}
