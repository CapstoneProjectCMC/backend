package com.codecampus.profile.service.kafka;

import com.codecampus.profile.entity.Post;
import com.codecampus.profile.repository.PostRepository;
import com.codecampus.profile.service.cache.PostCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.post.PostEvent;
import events.post.data.PostPayload;
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
  PostRepository postRepository;
  PostCacheService postCacheService;

  @KafkaListener(
      topics = "${app.event.post-events}",
      groupId = "profile-service")
  public void onPostEvent(String raw) {
    try {
      PostEvent evt = objectMapper.readValue(raw, PostEvent.class);
      String postId = evt.getId();

      switch (evt.getType()) {
        case CREATED, UPDATED -> {
          PostPayload p = evt.getPayload();
          if (p == null) {
            break;
          }

          Post node = postRepository.findByPostId(postId)
              .orElseGet(() -> Post.builder().postId(postId).build());
          node.setTitle(p.getTitle());
          postRepository.save(node);

          postCacheService.refresh(postId);
        }
        case DELETED -> {
          postCacheService.evictTwice(postId);
          postRepository.findByPostId(postId)
              .ifPresent(postRepository::delete);
        }
        default -> {
        }
      }
    } catch (Exception e) {
      log.error("[PostEvent] parse/handle failed: {}", e.getMessage(), e);
    }
  }
}