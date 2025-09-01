package com.codecampus.notification.repository;

import com.codecampus.notification.entity.WebSocketSessionDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WebSocketSessionRepository
    extends MongoRepository<WebSocketSessionDocument, String> {
  void deleteBySocketSessionId(String socketSessionId);

  List<WebSocketSessionDocument> findByUserId(String userId);
}