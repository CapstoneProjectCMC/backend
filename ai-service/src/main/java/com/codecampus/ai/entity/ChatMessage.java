package com.codecampus.ai.entity;

import com.codecampus.ai.constant.chat.ChatRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "chat_message",
    indexes = {
        @Index(name = "idx_chat_message_thread", columnList = "thread_id"),
        @Index(name = "idx_chat_message_created_at", columnList = "created_at")
    })
public class ChatMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "thread_id", nullable = false)
  ChatThread thread;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  ChatRole chatRole;

  @Lob
  @Column(nullable = false)
  String content;

  // Thông tin file đính kèm (nếu user gửi ảnh)
  @Column(name = "image_name")
  String imageOriginalName;

  @Column(name = "image_content_type")
  String imageContentType;

  @Column(name = "image_url")
  String imageUrl;

  @Column(name = "created_at", nullable = false, updatable = false)
  Instant createdAt;

  @Column(name = "updated_at")
  Instant updatedAt;

  @PrePersist
  void prePersist() {
    Instant now = Instant.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  void preUpdate() {
    updatedAt = Instant.now();
  }
}
