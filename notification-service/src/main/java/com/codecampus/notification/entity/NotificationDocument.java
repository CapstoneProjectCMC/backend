package com.codecampus.notification.entity;


import java.time.Instant;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@CompoundIndexes({
    @CompoundIndex(name = "recipient_createdAt_desc",
        def = "{'recipient': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "recipient_read_createdAt_desc",
        def = "{'recipient': 1, 'readStatus': 1, 'createdAt': -1}")
})
public class NotificationDocument {
  @MongoId
  String id;

  String recipient; // userId
  String channel; // "SOCKET" | "EMAIL" | "ALL"...
  String templateCode;
  Map<String, Object> param;
  String subject;
  String body;

  String status;

  /**
   * Trạng thái đọc
   */
  @Builder.Default
  String readStatus = "UNREAD"; // UNREAD | READ

  Instant readAt;

  /**
   * Trạng thái gửi đi (email, v.v.)
   */
  @Builder.Default
  String deliveryStatus = "PENDING"; // PENDING | SENT | FAILED

  Instant deliveredAt;

  @Builder.Default
  Instant createdAt = Instant.now();
}