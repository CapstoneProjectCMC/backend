package com.codecampus.search.service.kafka;

import com.codecampus.search.entity.OrganizationDocument;
import com.codecampus.search.mapper.OrganizationMapper;
import com.codecampus.search.repository.OrganizationDocumentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.org.OrganizationEvent;
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
public class OrganizationEventListener {

  OrganizationDocumentRepository repo;
  OrganizationMapper mapper;
  ObjectMapper objectMapper;

  @KafkaListener(topics = "${app.event.organization-events}", groupId = "search-service")
  public void onOrgEvent(String raw) {
    try {
      OrganizationEvent evt =
          objectMapper.readValue(raw, OrganizationEvent.class);
      switch (evt.getType()) {
        case CREATED -> {
          OrganizationDocument doc =
              mapper.toOrganizationDocumentFromOrganizationPayload(evt.getId(),
                  evt.getPayload());
          repo.save(doc);
        }
        case UPDATED, RESTORED -> {
          OrganizationDocument doc =
              repo.findById(evt.getId()).orElseGet(() ->
                  mapper.toOrganizationDocumentFromOrganizationPayload(
                      evt.getId(), evt.getPayload()));
          // clear soft-delete nếu RESTORED
          if (evt.getType() == OrganizationEvent.Type.RESTORED) {
            doc.setDeletedAt(null);
            doc.setDeletedBy(null);
          }
          mapper.toOrganizationDocumentFromOrganizationPayload(evt.getPayload(),
              doc);
          repo.save(doc);
        }
        case DELETED -> repo.findById(evt.getId()).ifPresent(doc -> {
          doc.setDeletedAt(Instant.now());
          doc.setDeletedBy("organization-service");
          repo.save(doc);
        });
      }
    } catch (Exception e) {
      log.error("[OrgEvent] parse/handle failed: {}", e.getMessage(), e);
    }
  }
}