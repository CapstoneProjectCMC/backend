package com.codecampus.notification.controller;

import com.codecampus.notification.dto.request.IntrospectRequest;
import com.codecampus.notification.dto.response.IntrospectResponse;
import com.codecampus.notification.entity.WebSocketSessionDocument;
import com.codecampus.notification.service.IdentityService;
import com.codecampus.notification.service.NotificationRealtimeService;
import com.codecampus.notification.service.WebSocketSessionService;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocketHandler {

  SocketIOServer server;
  IdentityService identityService;
  WebSocketSessionService wsService;
  NotificationRealtimeService realtimeService;

  @OnConnect
  public void onConnect(SocketIOClient client) {
    String token = client.getHandshakeData().getSingleUrlParam("token");
    IntrospectResponse res = identityService.introspect(
        IntrospectRequest.builder().token(token).build());

    if (!res.isValid()) {
      client.disconnect();
      return;
    }

    String userId = res.getUserId();
    // Join room theo userId => gửi đúng người
    client.joinRoom(userId);

    // Lưu session
    WebSocketSessionDocument doc = WebSocketSessionDocument.builder()
        .socketSessionId(client.getSessionId().toString())
        .userId(userId)
        .createdAt(Instant.now())
        .build();
    wsService.create(doc);

    realtimeService.pushUnreadBadge(userId);

    log.info("Socket connected: userId={} session={}", userId,
        doc.getSocketSessionId());
  }

  @OnDisconnect
  public void onDisconnect(SocketIOClient client) {
    UUID sid = client.getSessionId();
    wsService.deleteBySessionId(sid.toString());
    log.info("Socket disconnected: {}", sid);
  }

  @PostConstruct
  public void start() {
    server.start();
    server.addListeners(this);
    log.info("Socket server started");
  }

  @PreDestroy
  public void stop() {
    server.stop();
    log.info("Socket server stopped");
  }
}