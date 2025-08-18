package com.codecampus.ai.service;

import com.codecampus.ai.dto.request.ChatRequest;
import com.codecampus.ai.helper.AuthenticationHelper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatService {
    ChatClient chatClient;
    JdbcChatMemoryRepository jdbcChatMemoryRepository;

    public ChatService(
            ChatClient.Builder builder,
            JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        this.jdbcChatMemoryRepository = jdbcChatMemoryRepository;

        //TODO Nếu mà đầy bộ nhớ thì throw ra lỗi cụ thể
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(100)
                .build();

        chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .build())
                .build();
    }

    /* Backward-compat: giữ endpoint cũ (1 thread / user) */
    public String chat(com.codecampus.ai.dto.request.ChatRequest chatRequest) {
        String defaultThreadId = AuthenticationHelper.getMyUserId(); // cũ
        return chat(defaultThreadId, chatRequest);
    }

    public String chatWithImage(MultipartFile file, String message) {
        String defaultThreadId = AuthenticationHelper.getMyUserId(); // cũ
        return chatWithImage(defaultThreadId, file, message);
    }

    public String chat(
            String threadId,
            ChatRequest chatRequest) {

        SystemMessage sys = new SystemMessage("""
                You are CodeCampus.AI
                You should response with a formal voice
                """);
        UserMessage user = new UserMessage(chatRequest.message());
        Prompt prompt = new Prompt(sys, user);

        return chatClient
                .prompt(prompt)
                .options(ChatOptions.builder().temperature(0.3).build())
                .advisors(
                        adv -> adv.param(ChatMemory.CONVERSATION_ID, threadId))
                .call()
                .content();
    }

    public String chatWithImage(
            String threadId,
            MultipartFile file,
            String message) {
        Media media = Media.builder()
                .mimeType(MimeTypeUtils.parseMimeType(
                        file.getContentType()))
                .data(file.getResource())
                .build();

        return chatClient
                .prompt()
                .options(ChatOptions.builder().temperature(0.2).build())
                .system("You are CodeCampus.AI")
                .user(u -> u.media(media).text(message))
                .advisors(
                        adv -> adv.param(ChatMemory.CONVERSATION_ID, threadId))
                .call()
                .content();
    }
}
