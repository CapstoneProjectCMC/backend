package com.codecampus.profile.service.kafka;

import com.codecampus.profile.repository.ContestRepository;
import com.codecampus.profile.service.cache.ContestCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.contest.ContestEvent;
import events.contest.data.ContestPayload;
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
public class ContestEventListener {

  ObjectMapper objectMapper;
  ContestRepository contestRepository;
  ContestCacheService contestCacheService;

  @KafkaListener(
      topics = "${app.event.contest-events}",
      groupId = "profile-service"
  )
  public void onContestEvent(String raw) {
    try {
      ContestEvent event = objectMapper.readValue(raw, ContestEvent.class);
      switch (event.getType()) {
        case CREATED, UPDATED -> {
          ContestPayload p = event.getPayload();
          if (p == null) {
            break;
          }

          contestRepository.upsertContest(
              p.getId(),
              p.getTitle(),
              p.getStartTime(),
              p.getEndTime()
          );

          contestCacheService.refresh(event.getId());
        }
        case DELETED -> {
          contestCacheService.evictTwice(event.getId());

          contestRepository.findByContestId(event.getId())
              .ifPresent(contestRepository::delete);
        }
        default -> {
        }
      }
    } catch (Exception e) {
      log.error("[ContestEvent] parse/handle failed: {}", e.getMessage(), e);
    }
  }
}
