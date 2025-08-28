package com.codecampus.chat.repository;

import com.codecampus.chat.entity.ConversationMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationMemberRepository
    extends MongoRepository<ConversationMember, String> {
  Optional<ConversationMember> findByConversationIdAndUserId(
      String conversationId, String userId);

  List<ConversationMember> findAllByConversationId(
      String conversationId);

  List<ConversationMember> findAllByUserId(
      String userId);

  void deleteByConversationIdAndUserId(
      String conversationId,
      String userId);

  void deleteAllByConversationId(
      String conversationId);
}