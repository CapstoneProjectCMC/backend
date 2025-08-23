package com.codecampus.ai.repository;

import com.codecampus.ai.entity.ChatThread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatThreadRepository
        extends JpaRepository<ChatThread, String> {
    List<ChatThread> findByUserIdOrderByUpdatedAtDesc(String userId);

    Optional<ChatThread> findByIdAndUserId(String id, String userId);
}
