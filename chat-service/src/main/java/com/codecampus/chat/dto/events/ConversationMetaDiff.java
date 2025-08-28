package com.codecampus.chat.dto.events;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationMetaDiff {
  String nameBefore;
  String nameAfter;
  String avatarUrlBefore;
  String avatarUrlAfter;
  String topicBefore;
  String topicAfter;
}