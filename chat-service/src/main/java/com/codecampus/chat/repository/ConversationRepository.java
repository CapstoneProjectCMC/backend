package com.codecampus.chat.repository;

import com.codecampus.chat.entity.Conversation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository
    extends MongoRepository<Conversation, String> {
  Optional<Conversation> findByParticipantsHash(
      String participantsHash);

  @Query("{'participants.userId' : ?0}")
  List<Conversation> findAllByParticipantIdsContains(
      String participantId);

  @Query(value = "{'participants.userId' : ?0}")
  Page<Conversation> findAllByParticipantIdsContains(
      String participantId,
      Pageable pageable);
}
