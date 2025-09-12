package com.codecampus.chat.helper;

import com.codecampus.chat.dto.events.EventEnvelope;
import com.codecampus.chat.entity.Conversation;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EventHelper {

  ConversationHelper conversationHelper;
  ChatMessageHelper chatMessageHelper;

  public EventEnvelope.EventEnvelopeBuilder<Object> baseEnvelope(
      String type,
      Conversation conv,
      String actorId) {
    return EventEnvelope.builder()
        .type(type)
        .at(Instant.now())
        .actor(conversationHelper.findParticipant(conv, actorId))
        .conversation(
            chatMessageHelper.toConversationResponseFromConversation(conv));
  }
}
