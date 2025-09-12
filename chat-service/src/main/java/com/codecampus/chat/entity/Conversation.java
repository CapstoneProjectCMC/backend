package com.codecampus.chat.entity;

import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "conversation")
public class Conversation {
  @MongoId
  String id;

  String type; // GROUP, DIRECT

  @Indexed(unique = true, sparse = true)
  String participantsHash;

  List<ParticipantInfo> participants;

  String name; // tên nhóm (GROUP)
  String avatarUrl; // avatar nhóm (GROUP)
  String topic; // mô tả / chủ đề nhóm (GROUP)

  String ownerId; // chủ nhóm (GROUP)
  List<String> adminIds; // admin phụ (GROUP)

  Instant createdDate;
  Instant modifiedDate;

  Boolean deleted;
}
