package com.codecampus.payment.service.kafka;

import com.codecampus.payment.service.cache.UserSummaryCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.user.UserProfileEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileEventListener {

  private final ObjectMapper objectMapper;
  private final UserSummaryCacheService userSummaryCacheService;

  @KafkaListener(
      topics = "${app.event.profile-events}",
      groupId = "payment-service"
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
        default -> {
        }
      }
    } catch (Exception e) {
      log.error("[ProfileEvent] handle failed: {}", e.getMessage(), e);
    }
  }
}