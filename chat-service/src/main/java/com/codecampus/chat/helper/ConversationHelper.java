package com.codecampus.chat.helper;

import com.codecampus.chat.entity.Conversation;
import com.codecampus.chat.entity.ParticipantInfo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ConversationHelper {

  public ParticipantInfo findParticipant(
      Conversation conv, String userId) {
    return conv.getParticipants().stream()
        .filter(p -> userId.equals(p.getUserId()))
        .findFirst()
        .orElse(null);
  }
}
