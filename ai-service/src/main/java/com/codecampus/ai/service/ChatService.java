package com.codecampus.ai.service;

import com.codecampus.ai.dto.request.ChatRequest;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatService
{
  ChatClient chatClient;

  public ChatService(ChatClient.Builder builder)
  {
    chatClient = builder.build();
  }

  public String chat(ChatRequest request)
  {
    return chatClient
        .prompt(request.getMessage())
        .call()
        .content();
  }
}
