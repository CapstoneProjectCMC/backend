package com.codecampus.chat.helper;

import com.codecampus.chat.entity.WebSocketSession;
import com.codecampus.chat.repository.WebSocketSessionRepository;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebSocketHelper {
  SocketIOServer socketIOServer;
  WebSocketSessionRepository webSocketSessionRepository;
  ObjectMapper objectMapper;

  public void pushToUsers(
      Collection<String> userIds,
      String event,
      Object payload) {
    try {
      String json = objectMapper.writeValueAsString(payload);
      Map<String, WebSocketSession> map =
          webSocketSessionRepository.findAllByUserIdIn(userIds)
              .stream()
              .collect(Collectors.toMap(WebSocketSession::getSocketSessionId,
                  Function.identity()));
      socketIOServer.getAllClients().forEach(client -> {
        WebSocketSession ws = map.get(client.getSessionId().toString());
        if (ws != null) {
          client.sendEvent(event, json);
        }
      });
    } catch (Exception e) {
      // swallow
    }
  }
}
