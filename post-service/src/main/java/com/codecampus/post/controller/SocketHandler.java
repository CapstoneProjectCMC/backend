package com.codecampus.post.controller;

import com.codecampus.post.dto.data.SubscribePostPayload;
import com.codecampus.post.dto.request.IntrospectRequest;
import com.codecampus.post.dto.response.IntrospectResponse;
import com.codecampus.post.entity.Post;
import com.codecampus.post.helper.PostHelper;
import com.codecampus.post.repository.PostRepository;
import com.codecampus.post.service.IdentityService;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
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

  private static final String ATTR_USER_ID = "userId";
  private static final String ROOM_PREFIX = "post:";

  SocketIOServer server;
  IdentityService identityService;
  PostRepository postRepository;
  PostHelper postHelper;

  @PostConstruct
  public void start() {
    server.start();
    server.addListeners(this);
    log.info("[WS] Socket server started on {}",
        server.getConfiguration().getPort());
  }

  @PreDestroy
  public void stop() {
    server.stop();
    log.info("[WS] Socket server stopped");
  }


  @OnConnect
  public void onConnect(SocketIOClient client) {
    HandshakeData hs = client.getHandshakeData();
    String token = hs.getSingleUrlParam("token");

    IntrospectResponse introspect = identityService.introspect(
        IntrospectRequest.builder().token(token).build());

    if (introspect == null || !introspect.isValid()) {
      log.warn("[WS] Invalid token -> disconnect {}", client.getSessionId());
      client.disconnect();
      return;
    }

    client.set(ATTR_USER_ID, introspect.getUserId());
    log.info("[WS] {} connected as user {}", client.getSessionId(),
        introspect.getUserId());
  }

  @OnDisconnect
  public void onDisconnect(SocketIOClient client) {
    log.info("[WS] {} disconnected", client.getSessionId());
  }

  // Client gửi: socket.emit('post:subscribe', { postId: '...' })
  @OnEvent("post:subscribe")
  public void subscribePost(
      SocketIOClient client,
      SubscribePostPayload payload) {
    if (payload == null || payload.postId() == null) {
      return;
    }
    String userId = client.get(ATTR_USER_ID);
    if (userId == null) {
      client.disconnect();
      return;
    }

    Post post = postRepository.findById(payload.postId()).orElse(null);
    if (post == null || !postHelper.canView(post, userId)) {
      client.sendEvent("error", "NOT_AUTHORIZED");
      return;
    }
    String room = ROOM_PREFIX + payload.postId();
    client.joinRoom(room);
    client.sendEvent("post:subscribed", payload.postId());
    log.info("[WS] {} joined room {}", userId, room);
  }

  @OnEvent("post:unsubscribe")
  public void unsubscribePost(
      SocketIOClient client,
      SubscribePostPayload payload) {
    if (payload == null || payload.postId() == null) {
      return;
    }
    String room = ROOM_PREFIX + payload.postId();
    client.leaveRoom(room);
    client.sendEvent("post:unsubscribed", payload.postId());
    log.info("[WS] left room {}", room);
  }
}
