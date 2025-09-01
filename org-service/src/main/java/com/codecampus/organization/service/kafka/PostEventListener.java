package com.codecampus.organization.service.kafka;

import com.codecampus.organization.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.post.PostEvent;
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
public class PostEventListener {

  ObjectMapper objectMapper;
  PostService orgPostService;

  @KafkaListener(
      topics = "${app.event.post-events:post-events}",
      groupId = "organization-service"
  )
  public void onPostEvent(String raw) {
    try {
      PostEvent evt = objectMapper.readValue(raw, PostEvent.class);
      switch (evt.getType()) {
        case CREATED, UPDATED -> orgPostService
            .addOrUpdateFromPostPayload(evt.getId(), evt.getPayload());
        case DELETED -> orgPostService.softDeleteByPostId(evt.getId());
        default -> {
        }
      }
    } catch (Exception e) {
      log.error("[PostEventListener] Parse/handle failed: {}", e.getMessage(),
          e);
    }
  }
}