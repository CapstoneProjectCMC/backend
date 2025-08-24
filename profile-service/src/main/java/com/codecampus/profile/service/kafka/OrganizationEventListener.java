package com.codecampus.profile.service.kafka;

import com.codecampus.constant.ScopeType;
import com.codecampus.profile.entity.Org;
import com.codecampus.profile.repository.OrgRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import events.org.OrganizationEvent;
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

  OrgRepository orgRepository;
  ObjectMapper objectMapper;

  @KafkaListener(
      topics = "${app.event.organization-events}",
      groupId = "profile-service"
  )
  public void onOrganizationEvent(String raw) {
    try {
      OrganizationEvent event =
          objectMapper.readValue(raw, OrganizationEvent.class);
      String key =
          toOrgIdFromScopeType(event.getScopeType(), event.getId());

      switch (event.getType()) {
        case CREATED, UPDATED, RESTORED -> {
          Org org = orgRepository
              .findByOrgId(key)
              .orElseGet(() -> Org.builder().orgId(key).build());
          if (event.getPayload() != null) {
            org.setOrgName(event.getPayload().getName());
            org.setDescription(event.getPayload().getDescription());
            org.setLogoUrl(event.getPayload().getLogoUrl());
          }
          orgRepository.save(org);
        }
        case DELETED -> orgRepository.findByOrgId(key)
            .ifPresent(orgRepository::delete);
      }
    } catch (Exception e) {
      log.error("[Kafka] org-event parse/fail: {}", raw, e);
      throw new RuntimeException(e);
    }
  }

  private String toOrgIdFromScopeType(ScopeType scopeType, String id) {
    return switch (scopeType) {
      case ORGANIZATION -> "ORG:" + id;
      case GRADE -> "GRADE:" + id;
      case CLASS -> "CLASS:" + id;
    };
  }
}
