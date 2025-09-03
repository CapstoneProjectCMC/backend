package com.codecampus.notification.dto.response;


import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationView {
  String id;

  String recipient;     // userId (tham khảo)
  String channel;       // SOCKET/EMAIL/ALL...
  String templateCode;

  String subject;
  String body;
  Map<String, Object> param;

  String readStatus;      // READ | UNREAD
  Instant readAt;

  String deliveryStatus;  // PENDING | SENT | FAILED
  Instant deliveredAt;

  Instant createdAt;
}
