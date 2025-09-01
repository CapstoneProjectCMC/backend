package com.codecampus.notification.entity;


import jakarta.persistence.Id;
import java.time.Instant;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationDocument {
  @Id
  String id;

  String recipient; // userId
  String channel; // "SOCKET" | ...
  String templateCode;
  Map<String, Object> param;
  String subject;
  String body;

  @Builder.Default
  String status = "UNREAD"; // UNREAD | READ

  @Builder.Default
  Instant createdAt = Instant.now();
  Instant readAt;
}