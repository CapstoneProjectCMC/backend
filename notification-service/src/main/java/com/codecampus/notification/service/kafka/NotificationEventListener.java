package com.codecampus.notification.service.kafka;

import com.codecampus.notification.entity.NotificationDocument;
import com.codecampus.notification.repository.NotificationRepository;
import com.codecampus.notification.service.EmailService;
import com.codecampus.notification.service.SocketPushService;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.notification.NotificationEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationEventListener {

  ObjectMapper objectMapper;
  NotificationRepository notificationRepository;
  SocketPushService socketPushService;
  EmailService emailService;

  @KafkaListener(
      topics = "${app.event.notification-events}",
      groupId = "notification-service"
  )
  public void onNotificationEvent(String raw) {
    try {
      NotificationEvent evt =
          objectMapper.readValue(raw, NotificationEvent.class);

      // 1) Lưu Mongo
      NotificationDocument doc = NotificationDocument.builder()
          .recipient(evt.getRecipient())
          .channel(evt.getChannel())
          .templateCode(evt.getTemplateCode())
          .param(evt.getParam())
          .subject(evt.getSubject())
          .body(evt.getBody())
          // readStatus = UNREAD (default)
          // deliveryStatus = PENDING (default)
          .build();
      notificationRepository.save(doc);

      // 2) Đẩy realtime qua Socket.IO cho đúng user
      Map<String, Object> payload = new HashMap<>();
      payload.put("id", doc.getId());
      payload.put("templateCode", doc.getTemplateCode());
      payload.put("subject", doc.getSubject());
      payload.put("body", doc.getBody());
      payload.put("param", doc.getParam());
      payload.put("createdAt", doc.getCreatedAt());
      payload.put("readStatus", doc.getReadStatus());

      // 3) Gửi đúng kênh
      boolean wantSocket = hasChannel(evt.getChannel(), "SOCKET") ||
          hasChannel(evt.getChannel(), "ALL");
      boolean wantEmail = hasChannel(evt.getChannel(), "EMAIL") ||
          hasChannel(evt.getChannel(), "ALL");

      if (wantSocket) {
        socketPushService.pushToUser(evt.getRecipient(), payload);
      }

      if (wantEmail) {
        String to = resolveEmail(evt);
        if (to == null || to.isBlank()) {
          log.warn(
              "[Notification] missing recipient email, skip EMAIL for doc={}",
              doc.getId());
        } else {
          String subject = applyTemplate(evt.getSubject(), evt.getParam());
          String body = applyTemplate(evt.getBody(), evt.getParam());
          emailService.send(to, subject, body);

          // Cập nhật trạng thái gửi
          doc.setDeliveryStatus("SENT");
          doc.setDeliveredAt(java.time.Instant.now());
          notificationRepository.save(doc);
        }
      }

      log.info("[Notification] saved+dispatched channels='{}' to recipient={}",
          evt.getChannel(), evt.getRecipient());

    } catch (Exception e) {
      log.error("[Notification] handle failed: {}", e.getMessage(), e);
    }
  }

  private boolean hasChannel(String raw, String target) {
    if (raw == null) {
      return false;
    }
    for (String s : raw.split(",")) {
      if (s.trim().equalsIgnoreCase(target)) {
        return true;
      }
    }
    return false;
  }

  private String resolveEmail(NotificationEvent evt) {
    if (evt.getParam() == null) {
      return null;
    }
    Object v = evt.getParam().get("email");
    if (v == null) {
      v = evt.getParam().get("recipientEmail");
    }
    return v != null ? String.valueOf(v) : null;
  }

  private String applyTemplate(String text, Map<String, Object> param) {
    if (text == null) {
      return null;
    }
    if (param == null || param.isEmpty()) {
      return text;
    }
    String out = text;
    for (var e : param.entrySet()) {
      out = out.replace("{{" + e.getKey() + "}}", String.valueOf(e.getValue()));
    }
    return out;
  }
}