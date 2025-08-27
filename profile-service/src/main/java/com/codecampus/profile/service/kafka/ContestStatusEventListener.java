package com.codecampus.profile.service.kafka;

import com.codecampus.profile.entity.Contest;
import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.entity.properties.contest.ContestStatus;
import com.codecampus.profile.repository.ContestRepository;
import com.codecampus.profile.repository.UserProfileRepository;
import com.codecampus.profile.service.cache.ContestStatusCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import dtos.ContestStatusDto;
import events.contest.ContestStatusEvent;
import java.time.Instant;
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
public class ContestStatusEventListener {

  ObjectMapper objectMapper;
  UserProfileRepository userProfileRepository;
  ContestRepository contestRepository;
  ContestStatusCacheService statusCache;

  @KafkaListener(
      topics = "${app.event.contest-status-events}",
      groupId = "profile-service")
  public void onContestStatus(String raw) {
    try {
      ContestStatusEvent evt =
          objectMapper.readValue(raw, ContestStatusEvent.class);
      if (evt.getPayload() == null) {
        return;
      }
      upsert(evt.getPayload());
    } catch (Exception e) {
      log.error("[ContestStatusEvent] handle failed: {}", raw, e);
    }
  }

  void upsert(ContestStatusDto p) {
    var userOpt = userProfileRepository
        .findActiveByUserId(p.studentId());
    if (userOpt.isEmpty()) {
      log.warn("User {} not found, skip status", p.studentId());
      return;
    }
    UserProfile user = userOpt.get();

    Contest contest = contestRepository.mergeByContestId(p.contestId());

    /* xoá quan hệ cũ nếu có */
    user.getContests().removeIf(cs -> cs.getContest() != null
        && p.contestId().equals(cs.getContest().getContestId()));

    /* thêm quan hệ mới */
    user.getContests().add(
        ContestStatus.builder()
            .contest(contest)
            .state(p.state())
            .rank(p.rank())
            .score(p.score())
            .updatedAt(p.updatedAt() == null ? Instant.now() : p.updatedAt())
            .build());

    userProfileRepository.save(user);
    statusCache.refresh(p.studentId(), p.contestId());
  }
}