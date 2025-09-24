package com.codecampus.search.mapper;

import com.codecampus.search.entity.OrganizationDocument;
import events.org.data.OrganizationPayload;
import java.time.Instant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

  default OrganizationDocument toOrganizationDocumentFromOrganizationPayload(
      String id,
      OrganizationPayload p) {
    if (p == null) {
      return null;
    }
    return OrganizationDocument.builder()
        .id(id)
        .name(p.getName())
        .description(p.getDescription())
        .ownerId(p.getOwnerId())
        .logoUrl(p.getLogoUrl())
        .email(p.getEmail())
        .phone(p.getPhone())
        .address(p.getAddress())
        .status(p.getStatus())
        .createdAt(Instant.now())
        .updatedAt(p.getUpdatedAt() != null ? p.getUpdatedAt() : Instant.now())
        .build();
  }

  default void toOrganizationDocumentFromOrganizationPayload(
      OrganizationPayload p,
      OrganizationDocument doc) {
    if (p == null || doc == null) {
      return;
    }
    doc.setName(p.getName());
    doc.setDescription(p.getDescription());
    doc.setOwnerId(p.getOwnerId());
    doc.setLogoUrl(p.getLogoUrl());
    doc.setEmail(p.getEmail());
    doc.setPhone(p.getPhone());
    doc.setAddress(p.getAddress());
    doc.setStatus(p.getStatus());
    doc.setUpdatedAt(
        p.getUpdatedAt() != null ? p.getUpdatedAt() : Instant.now());
  }
}