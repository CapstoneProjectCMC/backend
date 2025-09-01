package com.codecampus.search.service.kafka;

import com.codecampus.search.repository.PostDocumentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.post.PostAccessEvent;
import java.util.HashSet;
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
public class PostAccessEventListener {

  PostDocumentRepository postRepo;
  ObjectMapper objectMapper;

  @KafkaListener(topics = "${app.event.post-access-events}", groupId = "search-service")
  public void onAccessEvent(String raw) {
    try {
      PostAccessEvent evt = objectMapper.readValue(raw, PostAccessEvent.class);
      postRepo.findById(evt.getPostId()).ifPresent(doc -> {
        if (doc.getAllowUserIds() == null) {
          doc.setAllowUserIds(new HashSet<>());
        }
        if (doc.getExcludeUserIds() == null) {
          doc.setExcludeUserIds(new HashSet<>());
        }
        switch (evt.getType()) {
          case UPSERT -> {
            if (Boolean.TRUE.equals(evt.getIsExcluded())) {
              doc.getExcludeUserIds().add(evt.getUserId());
              doc.getAllowUserIds().remove(evt.getUserId());
            } else {
              doc.getAllowUserIds().add(evt.getUserId());
              doc.getExcludeUserIds().remove(evt.getUserId());
            }
          }
          case BULK_DELETE -> {
            doc.getAllowUserIds().remove(evt.getUserId());
            doc.getExcludeUserIds().remove(evt.getUserId());
          }
          case DELETE_ALL_FOR_POST -> {
            doc.setAllowUserIds(new HashSet<>());
            doc.setExcludeUserIds(new HashSet<>());
          }
        }
        postRepo.save(doc);
      });
    } catch (Exception e) {
      log.error("[PostAccessEvent] handle failed: {}", e.getMessage(), e);
    }
  }
}