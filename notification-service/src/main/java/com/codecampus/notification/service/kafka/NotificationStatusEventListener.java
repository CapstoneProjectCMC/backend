package com.codecampus.notification.service.kafka;

import com.codecampus.notification.service.NotificationStatusService;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.notification.NotificationStatusEvent;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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
public class NotificationStatusEventListener {

  ObjectMapper objectMapper;
  NotificationStatusService statusService;

  @KafkaListener(
      topics = "${app.event.notification-status-events}",
      groupId = "notification-service"
  )
  public void onNotificationStatusEvent(String raw) {
    try {
      NotificationStatusEvent evt =
          objectMapper.readValue(raw, NotificationStatusEvent.class);

      if (evt.getRecipient() == null || evt.getRecipient().isBlank()) {
        log.warn("[Notification] Missing recipient in status event, skip: {}",
            raw);
        return;
      }

      Set<String> ids = resolveIds(evt.getId(), evt.getIds());
      Instant at = evt.getAt();

      switch (evt.getType()) {
        case MARK_READ -> {
          if (ids.isEmpty()) {
            log.warn("[Notification] MARK_READ requires id/ids");
            return;
          }
          statusService.markRead(evt.getRecipient(), ids, at);
        }
        case MARK_UNREAD -> {
          if (ids.isEmpty()) {
            log.warn("[Notification] MARK_UNREAD requires id/ids");
            return;
          }
          statusService.markUnread(evt.getRecipient(), ids);
        }
        case MARK_ALL_READ -> {
          statusService.markAllRead(evt.getRecipient(), evt.getBefore(), at);
        }
      }

    } catch (Exception e) {
      log.error("[Notification] status handle failed: {}", e.getMessage(), e);
    }
  }

  private Set<String> resolveIds(String id, Set<String> ids) {
    if (ids != null && !ids.isEmpty()) {
      return ids;
    }
    if (id != null && !id.isBlank()) {
      return new HashSet<>(Collections.singleton(id));
    }
    return Collections.emptySet();
  }
}