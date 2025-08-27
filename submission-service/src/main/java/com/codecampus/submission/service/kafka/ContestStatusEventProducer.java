package com.codecampus.submission.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dtos.ContestStatusDto;
import events.contest.ContestStatusEvent;
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
public class ContestStatusEventProducer {

  KafkaTemplate<String, String> kafkaTemplate;
  ObjectMapper objectMapper;

  @Value("${app.event.contest-status-events}")
  @NonFinal
  String CONTEST_STATUS_TOPIC;

  public void publishUpsert(ContestStatusDto contestStatusDto) {
    publish(ContestStatusEvent.Type.UPSERT, contestStatusDto);
  }

  public void publishDeleted(ContestStatusDto contestStatusDto) {
    publish(ContestStatusEvent.Type.DELETED, contestStatusDto);
  }


  public void publish(
      ContestStatusEvent.Type type,
      ContestStatusDto contestStatusDto) {

    ContestStatusEvent event = ContestStatusEvent.builder()
        .id(contestStatusDto.contestId())
        .type(type)
        .payload(contestStatusDto)
        .build();

    try {
      String json = objectMapper.writeValueAsString(event);
      String key =
          event.getPayload().contestId() + ":" + event.getPayload().studentId();

      kafkaTemplate.send(
          CONTEST_STATUS_TOPIC,
          key,
          json);
    } catch (JsonProcessingException e) {
      log.error("[Kafka] Serialize ContestStatusEvent fail", e);
      throw new RuntimeException(e);
    }
  }
}
