package com.codecampus.chat.service;

import com.codecampus.chat.entity.WebSocketSession;
import com.codecampus.chat.repository.WebSocketSessionRepository;
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

    WebSocketSessionRepository webSocketSessionRepository;

    public void createWebSocketSession(
            WebSocketSession webSocketSession) {
        webSocketSessionRepository.save(webSocketSession);
    }

    public void deleteWebSocketSession(
            String sessionId) {
        webSocketSessionRepository
                .deleteBySocketSessionId(sessionId);
    }
}
