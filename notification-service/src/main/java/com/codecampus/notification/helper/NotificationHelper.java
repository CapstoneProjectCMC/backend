package com.codecampus.notification.helper;

import com.codecampus.notification.dto.response.NotificationView;
import com.codecampus.notification.entity.NotificationDocument;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationHelper {
  public NotificationView toNotificationVewFromNotificationDocument(
      NotificationDocument d) {
    return NotificationView.builder()
        .id(d.getId())
        .recipient(d.getRecipient())
        .channel(d.getChannel())
        .templateCode(d.getTemplateCode())
        .subject(d.getSubject())
        .body(d.getBody())
        .param(d.getParam())
        .readStatus(d.getReadStatus())
        .readAt(d.getReadAt())
        .deliveryStatus(d.getDeliveryStatus())
        .deliveredAt(d.getDeliveredAt())
        .createdAt(d.getCreatedAt())
        .build();
  }
}
