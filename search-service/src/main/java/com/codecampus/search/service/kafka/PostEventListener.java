package com.codecampus.search.service.kafka;

import com.codecampus.search.dto.common.PageResponse;
import com.codecampus.search.dto.response.PostAccessResponseDto;
import com.codecampus.search.entity.PostDocument;
import com.codecampus.search.mapper.PostMapper;
import com.codecampus.search.repository.PostDocumentRepository;
import com.codecampus.search.repository.client.PostAccessClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.post.PostEvent;
import java.util.Set;
import java.util.stream.Collectors;
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

  PostDocumentRepository postRepo;
  PostMapper postMapper;
  ObjectMapper objectMapper;
  PostAccessClient accessClient;

  @KafkaListener(
      topics = "${app.event.post-events}",
      groupId = "search-service"
  )
  public void onPostEvent(String raw) {
    try {
      PostEvent evt = objectMapper.readValue(raw, PostEvent.class);
      switch (evt.getType()) {
        case CREATED, UPDATED -> {
          PostDocument doc =
              postMapper.toPostDocumentFromPostPayload(evt.getPayload());
          // load access (để lọc visible)
          PageResponse<PostAccessResponseDto> page =
              accessClient.internalGetAccessByPost(evt.getId(), 1, 1000)
                  .getResult();
          if (page != null && page.getData() != null) {
            Set<String> allow = page.getData().stream()
                .filter(a -> !a.isExcluded())
                .map(PostAccessResponseDto::getUserId)
                .collect(Collectors.toSet());
            Set<String> deny = page.getData().stream()
                .filter(PostAccessResponseDto::isExcluded)
                .map(PostAccessResponseDto::getUserId)
                .collect(Collectors.toSet());
            doc.setAllowUserIds(allow);
            doc.setExcludeUserIds(deny);
          }
          postRepo.save(doc);
        }
        case DELETED -> postRepo.deleteById(evt.getId());
      }
    } catch (Exception e) {
      log.error("[PostEvent] parse/handle failed: {}", e.getMessage(), e);
    }
  }
}