//package com.codecampus.ai.config.ai;
//
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
//import org.springframework.ai.chat.memory.ChatMemory;
//import org.springframework.ai.chat.memory.MessageWindowChatMemory;
//import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
//import org.springframework.ai.chat.model.ChatModel;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class AiConfig {
//    /**
//     * Lưu tối đa 100 message gần nhất / cuộc trò chuyện,
//     * persist xuống PostgreSQL qua JdbcChatMemoryRepository.
//     * Spring AI tự khởi tạo bảng SPRING_AI_CHAT_MEMORY nhờ
//     * spring.ai.chat.memory.repository.jdbc.initialize-schema=always
//     */
//    @Bean
//    public ChatMemory chatMemory(
//            JdbcChatMemoryRepository jdbcChatMemoryRepository) {
//        //TODO Nếu mà đầy bộ nhớ thì throw ra lỗi cụ thể
//        return MessageWindowChatMemory.builder()
//                .chatMemoryRepository(jdbcChatMemoryRepository)
//                .maxMessages(100)
//                .build();
//    }
//
//    /**
//     * ChatClient dùng cho toàn bộ service,
//     * gắn sẵn MessageChatMemoryAdvisor để tự động nạp/lưu memory.
//     */
//    @Bean
//    public ChatClient chatClient(
//            ChatModel chatModel,
//            ChatMemory memory) {
//        return ChatClient.builder(chatModel)
//                .defaultAdvisors(
//                        MessageChatMemoryAdvisor.builder(memory).build())
//                .build();
//    }
//}
