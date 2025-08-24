package com.codecampus.chat.service.kafka;

import com.codecampus.chat.entity.Conversation;
import com.codecampus.chat.mapper.UserMapper;
import com.codecampus.chat.repository.ConversationRepository;
import com.codecampus.chat.service.cache.UserSummaryCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import dtos.UserProfileSummary;
import events.user.UserProfileEvent;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileEventListener {

  ObjectMapper objectMapper;
  UserMapper userMapper;
  UserSummaryCacheService cache;
  ConversationRepository conversationRepository;

  @KafkaListener(
      topics = "${app.event.profile-events}",
      groupId = "chat-service"
  )
  public void onProfileEvent(String raw) {
    try {
      UserProfileEvent event =
          objectMapper.readValue(raw, UserProfileEvent.class);
      String userId = event.getId();

      switch (event.getType()) {
        case UPDATED, RESTORED -> {
          if (event.getPayload() != null) {
            UserProfileSummary userProfileSummary =
                userMapper.toUserProfileSummaryFromUserProfilePayload(
                    event.getPayload());
            cache.put(userId, userProfileSummary);
            syncParticipantSnapshot(userProfileSummary);
          } else {
            cache.evictTwice(userId);
          }
          log.info("[ProfileEvent] cached & synced {}", userId);
        }
        case DELETED -> {
          cache.evictTwice(userId);
          // Tùy chính sách: có thể xoá avatar/name hoặc để nguyên snapshot cũ
          log.info("[ProfileEvent] Evicted cache for {}", userId);
        }
        default -> {
        }
      }
    } catch (Exception e) {
      log.error("[ProfileEvent] parse/handle failed: {}", e.getMessage(),
          e);
    }
  }

  void syncParticipantSnapshot(UserProfileSummary userProfileSummary) {
    try {
      List<Conversation> conversations =
          conversationRepository.findAllByParticipantIdsContains(
              userProfileSummary.userId());

      for (Conversation conversation : conversations) {
        conversation.getParticipants().stream()
            .filter(p -> userProfileSummary.userId()
                .equals(p.getUserId()))
            .findFirst()
            .ifPresent(p -> {
              p.setUserId(userProfileSummary.userId());
              p.setUsername(userProfileSummary.username());
              p.setEmail(userProfileSummary.email());
              p.setActive(userProfileSummary.active());
              p.setDisplayName(userProfileSummary.displayName());
              p.setFirstName(userProfileSummary.firstName());
              p.setLastName(userProfileSummary.lastName());
              p.setAvatarUrl(userProfileSummary.avatarUrl());
            });
        conversationRepository.save(conversation);
      }
    } catch (Exception ex) {
      log.warn("[ProfileEvent] syncParticipantSnapshot error {}: {}",
          userProfileSummary.userId(), ex.getMessage());
    }
  }
}
