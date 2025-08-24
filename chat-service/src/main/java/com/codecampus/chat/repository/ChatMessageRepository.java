package com.codecampus.chat.repository;

import com.codecampus.chat.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository
        extends MongoRepository<ChatMessage, String> {

    Page<ChatMessage> findAllByConversationIdOrderByCreatedDateDesc(
            String conversationId,
            Pageable pageable);

    List<ChatMessage> findAllBySender_UserId(String userId);
}
