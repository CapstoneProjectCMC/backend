package com.codecampus.post.service.kafka;

import com.codecampus.post.service.cache.UserSummaryCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.user.UserProfileEvent;
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
  UserSummaryCacheService userSummaryCacheService;

  @KafkaListener(
      topics = "${app.event.profile-events}",
      groupId = "post-service"
  )
  public void onProfileEvent(String raw) {
    try {
      UserProfileEvent event =
          objectMapper.readValue(raw, UserProfileEvent.class);
      String userId = event.getId();
      switch (event.getType()) {
        case UPDATED, RESTORED -> {
          userSummaryCacheService.refresh(userId);
          log.info("[ProfileEvent] Refreshed user cache {}", userId);
        }
        case DELETED -> {
          userSummaryCacheService.evictTwice(userId);
          log.info("[ProfileEvent] Evicted user cache {}", userId);
        }
        default -> { /* no-op */ }
      }
    } catch (Exception e) {
      log.error("[ProfileEvent] parse/handle failed: {}", e.getMessage(), e);
    }
  }
}