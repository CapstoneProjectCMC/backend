package com.codecampus.notification.service;

import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationRealtimeService {

  private final NotificationService notificationService;
  private final SocketPushService socketPushService;

  /**
   * Tính và push số lượng UNREAD hiện tại cho user.
   * Event name: "notification-unread", payload: { "unread": long }
   */
  public void pushUnreadBadge(String recipient) {
    long unread = notificationService.countMyUnread(recipient);
    socketPushService.pushToUserEvent(
        recipient,
        "notification-unread",
        Map.of("unread", unread)
    );
    log.debug("[Notification] pushUnreadBadge recipient={} unread={}",
        recipient, unread);
  }
}