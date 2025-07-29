package com.codecampus.chat.controller;

import com.codecampus.chat.dto.request.IntrospectRequest;
import com.codecampus.chat.dto.response.IntrospectResponse;
import com.codecampus.chat.entity.WebSocketSession;
import com.codecampus.chat.service.IdentityService;
import com.codecampus.chat.service.WebSocketSessionService;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocketHandler {

    SocketIOServer server;
    IdentityService identityService;
    WebSocketSessionService webSocketSessionService;

    @OnConnect
    public void clientConnected(
            SocketIOClient socketIOClient) {
        // Get Token from request param
        String token = socketIOClient.getHandshakeData()
                .getSingleUrlParam("token");

        // Verify token
        IntrospectResponse introspectResponse = identityService
                .introspect(
                        IntrospectRequest.builder()
                                .token(token)
                                .build()
                );

        // If Token is invalid disconnect
        if (introspectResponse.isValid()) {
            // Persist webSocketSession
            WebSocketSession webSocketSession = WebSocketSession.builder()
                    .socketSessionId(socketIOClient.getSessionId().toString())
                    .userId(introspectResponse.getUserId())
                    .createdAt(Instant.now())
                    .build();
            webSocketSessionService.createWebSocketSession(webSocketSession);
        } else {
            socketIOClient.disconnect();
        }
    }

    @OnDisconnect
    public void clientDisconnected(
            SocketIOClient socketIOClient) {
        webSocketSessionService.deleteWebSocketSession(
                socketIOClient.getSessionId().toString());
    }

    @PostConstruct
    public void startServer() {
        server.start();
        server.addListeners(this);
        log.info("Socket server started");
    }

    @PreDestroy
    public void stopServer() {
        server.stop();
        log.info("Socket server stoped");
    }
}
