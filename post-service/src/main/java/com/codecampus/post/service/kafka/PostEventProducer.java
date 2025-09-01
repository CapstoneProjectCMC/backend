package com.codecampus.post.service.kafka;

import com.codecampus.post.entity.Post;
import com.codecampus.post.mapper.PostPayloadMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.post.PostEvent;
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
public class PostEventProducer {
  KafkaTemplate<String, String> kafkaTemplate;
  ObjectMapper objectMapper;
  PostPayloadMapper mapper;

  @Value("${app.event.post-events:post-events}")
  @NonFinal
  String POST_EVENTS_TOPIC;

  public void publishCreated(Post p) {
    publish(PostEvent.Type.CREATED, p);
  }

  public void publishUpdated(Post p) {
    publish(PostEvent.Type.UPDATED, p);
  }

  public void publishDeleted(Post p) {
    publish(PostEvent.Type.DELETED, p);
  }

  void publish(PostEvent.Type type, Post p) {
    try {
      var evt = PostEvent.builder()
          .type(type)
          .id(p.getPostId())
          .payload(type == PostEvent.Type.DELETED ? null :
              mapper.toPostPayloadFromPost(p))
          .build();
      String json = objectMapper.writeValueAsString(evt);
      kafkaTemplate.send(POST_EVENTS_TOPIC, p.getPostId(), json);
    } catch (Exception e) {
      log.error("[Kafka] PostEvent serialize/send failed", e);
      throw new RuntimeException(e);
    }
  }
}