package com.codecampus.ai.service;

import com.codecampus.ai.dto.response.chat.ThreadResponse;
import com.codecampus.ai.entity.ChatThread;
import com.codecampus.ai.helper.AuthenticationHelper;
import com.codecampus.ai.repository.ChatThreadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatThreadService {

    private final ChatThreadRepository repo;
    private final JdbcChatMemoryRepository memoryRepo;

    public List<ThreadResponse> myThreads() {
        String uid = AuthenticationHelper.getMyUserId();
        return repo.findByUserIdOrderByUpdatedAtDesc(uid).stream()
                .map(t -> new ThreadResponse(
                        t.getId(), t.getTitle(), t.getLastMessageAt(),
                        t.getCreatedAt(), t.getUpdatedAt()))
                .toList();
    }

    @Transactional
    public ThreadResponse create(String title) {
        String uid = AuthenticationHelper.getMyUserId();
        ChatThread t = ChatThread.builder()
                .userId(uid)
                .title((title == null || title.isBlank()) ?
                        "Cuộc trò chuyện mới" : title.trim())
                .build();
        repo.save(t);
        return new ThreadResponse(t.getId(), t.getTitle(), t.getLastMessageAt(),
                t.getCreatedAt(), t.getUpdatedAt());
    }

    @Transactional
    public ThreadResponse rename(String id, String title) {
        String uid = AuthenticationHelper.getMyUserId();
        ChatThread t = repo.findByIdAndUserId(id, uid).orElseThrow();
        t.setTitle((title == null || title.isBlank()) ? t.getTitle() :
                title.trim());
        repo.save(t);
        return new ThreadResponse(t.getId(), t.getTitle(), t.getLastMessageAt(),
                t.getCreatedAt(), t.getUpdatedAt());
    }

    @Transactional
    public void touch(String id) {
        String uid = AuthenticationHelper.getMyUserId();
        ChatThread t = repo.findByIdAndUserId(id, uid).orElseThrow();
        t.setLastMessageAt(Instant.now());
        repo.save(t);
    }

    @Transactional
    public void delete(String id) {
        String uid = AuthenticationHelper.getMyUserId();
        ChatThread t = repo.findByIdAndUserId(id, uid).orElseThrow();
        // xoá lịch sử hội thoại trong JDBC memory theo conversationId = threadId
        memoryRepo.deleteByConversationId(id);
        repo.delete(t);
    }
}
