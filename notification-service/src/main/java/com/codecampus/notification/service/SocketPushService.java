package com.codecampus.notification.service;

import com.corundumstudio.socketio.SocketIOServer;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SocketPushService {
  SocketIOServer server;

  /**
   * Gửi một event "notification" cho đúng userId (room = userId).
   * payload có thể là bất kỳ Map/POJO serializable nào.
   */
  public void pushToUser(
      String userId,
      Map<String, Object> payload) {
    server.getRoomOperations(userId)
        .sendEvent("notification", payload);
  }
}