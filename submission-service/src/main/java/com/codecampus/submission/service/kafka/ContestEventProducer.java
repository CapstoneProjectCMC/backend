package com.codecampus.submission.service.kafka;

import com.codecampus.submission.entity.Contest;
import com.codecampus.submission.mapper.ContestMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.contest.ContestEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContestEventProducer {

  KafkaTemplate<String, String> kafkaTemplate;
  ObjectMapper objectMapper;
  ContestMapper contestMapper;

  @Value("${app.event.contest-events}")
  @NonFinal
  String CONTEST_EVENTS_TOPIC;

  public void publishCreated(Contest contest) {
    publishEvent(ContestEvent.Type.CREATED, contest);
  }

  public void publishUpdated(Contest contest) {
    publishEvent(ContestEvent.Type.UPDATED, contest);
  }

  public void publishDeleted(Contest contest) {
    publishEvent(ContestEvent.Type.DELETED, contest);
  }

  void publishEvent(ContestEvent.Type type, Contest contest) {
    ContestEvent event = ContestEvent.builder()
        .type(type)
        .id(contest.getId())
        .payload(type == ContestEvent.Type.DELETED ? null :
            contestMapper.toContestPayloadFromContest(contest))
        .build();
    try {
      String json = objectMapper.writeValueAsString(event);
      kafkaTemplate.send(CONTEST_EVENTS_TOPIC, event.getId(), json);
    } catch (Exception e) {
      log.error("[Kafka] Contest serialize failed", e);
      throw new RuntimeException(e);
    }
  }
}
