package com.codecampus.post.service;

import com.codecampus.post.dto.data.DeletePayload;
import com.codecampus.post.dto.response.CommentResponseDto;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebsocketRealtimeService {

  private static final String ROOM_PREFIX = "post:";
  SocketIOServer server;

  public void commentCreated(String postId, CommentResponseDto payload) {
    broadcast(postId, "comment:created", payload);
  }

  public void commentUpdated(String postId, CommentResponseDto payload) {
    broadcast(postId, "comment:updated", payload);
  }

  public void commentDeleted(String postId, String commentId) {
    broadcast(postId, "comment:deleted", new DeletePayload(commentId));
  }

  private void broadcast(String postId, String event, Object data) {
    String room = ROOM_PREFIX + postId;
    server.getRoomOperations(room).sendEvent(event, data);
    log.debug("[WS] {} -> room {} sent", event, room);
  }

}
