package com.codecampus.chat.entity;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document("conversation_member")
@CompoundIndexes({
    @CompoundIndex(
        name = "idx_conv_user",
        def = "{'conversationId':1,'userId':1}",
        unique = true
    )
})
public class ConversationMember {
  @MongoId
  String id;

  String conversationId;

  @Indexed(name = "idx_user")
  String userId;

  // trạng thái đọc
  Instant lastReadAt; // thời điểm đã đọc đến
  String lastReadMessageId; // tuỳ chọn

  // tuỳ chọn: đếm chưa đọc cache hoá (không bắt buộc)
  Long unreadCount;

  // notify
  Instant mutedUntil; // null = không mute

  // vai trò ở cấp thành viên (có thể thừa nếu đã quản bằng ownerId/adminIds)
  // MEMBER/ADMIN/OWNER (đồng bộ cùng Conversation.ownerId/adminIds)
  String role;

  Instant joinedAt;
}