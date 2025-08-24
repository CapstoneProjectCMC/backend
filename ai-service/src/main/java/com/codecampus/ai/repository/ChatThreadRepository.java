package com.codecampus.ai.repository;

import com.codecampus.ai.entity.ChatThread;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatThreadRepository
    extends JpaRepository<ChatThread, String> {
  List<ChatThread> findByUserIdOrderByUpdatedAtDesc(String userId);

  Optional<ChatThread> findByIdAndUserId(String id, String userId);
}
