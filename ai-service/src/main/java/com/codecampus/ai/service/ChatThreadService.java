package com.codecampus.ai.service;

import com.codecampus.ai.dto.response.chat.MessageResponse;
import com.codecampus.ai.dto.response.chat.ThreadDetailResponse;
import com.codecampus.ai.dto.response.chat.ThreadResponse;
import com.codecampus.ai.entity.ChatThread;
import com.codecampus.ai.exception.AppException;
import com.codecampus.ai.exception.ErrorCode;
import com.codecampus.ai.helper.AuthenticationHelper;
import com.codecampus.ai.mapper.ChatThreadMapper;
import com.codecampus.ai.repository.ChatThreadRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatThreadService {

    ChatThreadRepository chatThreadRepository;
    JdbcChatMemoryRepository jdbcChatMemoryRepository;
    ChatThreadMapper chatThreadMapper;
    ChatMessageService chatMessageService;

    public List<ThreadResponse> myThreads() {
        String userId = AuthenticationHelper.getMyUserId();
        return chatThreadRepository
                .findByUserIdOrderByUpdatedAtDesc(userId)
                .stream()
                .map(chatThreadMapper::toThreadResponseFromChatThread)
                .toList();
    }

    @Transactional(readOnly = true)
    public ThreadDetailResponse getThread(
            String id) {
        String userId = AuthenticationHelper.getMyUserId();
        ChatThread chatThread = chatThreadRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AppException(
                        ErrorCode.CHAT_THREAD_NOT_FOUND));

        List<MessageResponse> messages = chatMessageService.listMessages(id);
        return chatThreadMapper.toThreadDetailResponseFromChatThreadAndMessageResponseList(
                chatThread,
                messages);
    }

    @Transactional
    public ThreadResponse createThread(String title) {
        String userId = AuthenticationHelper.getMyUserId();
        ChatThread chatThread = ChatThread.builder()
                .userId(userId)
                .title((title == null || title.isBlank()) ?
                        "Cuộc trò chuyện mới" : title.trim())
                .build();
        chatThreadRepository.save(chatThread);

        return chatThreadMapper.toThreadResponseFromChatThread(chatThread);
    }

    @Transactional
    public ThreadResponse renameThread(
            String id, String title) {
        String userId = AuthenticationHelper.getMyUserId();
        ChatThread chatThread = chatThreadRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.CHAT_THREAD_NOT_FOUND)
                );
        chatThread.setTitle(
                (title == null || title.isBlank()) ? chatThread.getTitle() :
                        title.trim());
        chatThreadRepository.save(chatThread);
        return chatThreadMapper.toThreadResponseFromChatThread(chatThread);
    }

    @Transactional
    public void touchThread(String id) {
        String userId = AuthenticationHelper.getMyUserId();
        ChatThread chatThread = chatThreadRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.CHAT_THREAD_NOT_FOUND)
                );
        chatThread.setLastMessageAt(Instant.now());
        chatThreadRepository.save(chatThread);
    }

    @Transactional
    public void delete(String id) {
        String userId = AuthenticationHelper.getMyUserId();
        ChatThread chatThread = chatThreadRepository
                .findByIdAndUserId(id, userId)
                .orElseThrow();
        // Xoá lịch sử hội thoại trong JDBC memory theo conversationId = threadId
        jdbcChatMemoryRepository.deleteByConversationId(id);
        chatThreadRepository.delete(chatThread);
    }
}
