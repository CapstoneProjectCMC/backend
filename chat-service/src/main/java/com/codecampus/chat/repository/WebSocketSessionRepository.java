package com.codecampus.chat.repository;

import com.codecampus.chat.entity.WebSocketSession;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebSocketSessionRepository
    extends MongoRepository<WebSocketSession, String> {
  void deleteBySocketSessionId(
      String socketSessionId);

  List<WebSocketSession> findAllByUserIdIn(
      List<String> userIds);
}

