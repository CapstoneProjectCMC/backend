package com.codecampus.notification.service;

import com.codecampus.notification.entity.WebSocketSessionDocument;
import com.codecampus.notification.repository.WebSocketSessionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebSocketSessionService {
  WebSocketSessionRepository repo;

  public void create(WebSocketSessionDocument doc) {
    repo.save(doc);
  }

  public void deleteBySessionId(String sessionId) {
    repo.deleteBySocketSessionId(sessionId);
  }
}