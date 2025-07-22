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

    public String chatWithImage(
            MultipartFile file,
            String message) {
        String conversationId = AuthenticationHelper.getMyUserId();

        Media media = Media.builder()
                .mimeType(MimeTypeUtils.parseMimeType(
                        file.getContentType()))
                .data(file.getResource())
                .build();

        ChatOptions chatOptions = ChatOptions.builder()
                .temperature(0D)
                .build();

        return chatClient.prompt()
                .options(chatOptions)
                .system("You are CodeCampus.AI")
                .user(promptUserSpec -> promptUserSpec.media(media)
                        .text(message))
                .advisors(advisorSpec -> {
                    advisorSpec.param(
                            ChatMemory.CONVERSATION_ID, conversationId
                    );
                })
                .call()
                .content();
    }

    public String chat(ChatRequest chatRequest) {

        String conversationId = AuthenticationHelper.getMyUserId();
        SystemMessage systemMessage = new SystemMessage(
                """
                        You are CodeCampus.AI
                        You should response with a formal voice
                        """
        );

        UserMessage userMessage = new UserMessage(
                chatRequest.message()
        );

        Prompt prompt = new Prompt(systemMessage, userMessage);

        return chatClient
                .prompt(prompt)
                .advisors(advisorSpec -> {
                    advisorSpec.param(
                            ChatMemory.CONVERSATION_ID, conversationId
                    );
                })
                .call()
                .content();
    }
}
