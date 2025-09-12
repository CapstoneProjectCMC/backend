package com.codecampus.chat.dto.response;

import com.codecampus.chat.entity.ParticipantInfo;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// Mặc định cho phép response cả null khi Dev
// Khi build thì KHÔNG response null
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConversationResponse {
  String id;
  String type; // GROUP; DIRECT
  String participantsHash;
  String conversationAvatar;
  String conversationName;
  String topic;
  String ownerId;
  List<String> adminIds;
  List<ParticipantInfo> participants;
  Instant createdDate;
  Instant modifiedDate;

  Long unreadCount;
  String myRole; // OWNER/ADMIN/MEMBER
  Instant mutedUntil;
}
