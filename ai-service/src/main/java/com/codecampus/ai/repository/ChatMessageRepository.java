package com.codecampus.ai.repository;

import com.codecampus.ai.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, String> {
    List<ChatMessage> findByThreadIdOrderByCreatedAtAsc(
            String threadId);

    void deleteByThreadId(
            String threadId);
}