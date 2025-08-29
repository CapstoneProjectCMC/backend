package com.codecampus.organization.service;

import com.codecampus.constant.ScopeType;
import com.codecampus.organization.dto.request.CreateOrganizationForm;
import com.codecampus.organization.dto.request.UpdateOrganizationForm;
import com.codecampus.organization.dto.response.OrganizationResponse;
import com.codecampus.organization.entity.Organization;
import com.codecampus.organization.helper.AuthenticationHelper;
import com.codecampus.organization.helper.OrganizationHelper;
import com.codecampus.organization.mapper.OrganizationMapper;
import com.codecampus.organization.repository.OrganizationRepository;
import com.codecampus.organization.service.kafka.OrganizationEventProducer;
import events.org.OrganizationEvent;
import events.org.data.OrganizationPayload;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationService {
  OrganizationRepository organizationRepository;
  OrganizationMapper organizationMapper;
  OrganizationEventProducer eventProducer;
  OrganizationHelper organizationHelper;

  @Transactional
  public OrganizationResponse createOrganization(
      CreateOrganizationForm form) {

    String logoUrl = organizationHelper.uploadIfAny(form.getLogo());

    Organization o = Organization.builder()
        .name(form.getName())
        .description(form.getDescription())
        .logoUrl(logoUrl)
        .email(form.getEmail())
        .phone(form.getPhone())
        .address(form.getAddress())
        .status(form.getStatus())
        .build();

    o = organizationRepository.save(o);

    // publish CREATED
    OrganizationPayload payload =
        organizationMapper.toOrganizationPayloadFromOrganization(o);
    eventProducer.publish(OrganizationEvent.builder()
        .type(OrganizationEvent.Type.CREATED)
        .id(o.getId())
        .scopeType(ScopeType.Organization)
        .payload(payload)
        .build());

    return organizationMapper.toOrganizationResponseFromOrganization(o);
  }

  @Transactional
  public OrganizationResponse updateOrganization(
      String id,
      UpdateOrganizationForm form) {
    Organization o = organizationRepository.findById(id)
        .orElseThrow(
            () -> new IllegalArgumentException("Organization not found"));

    if (StringUtils.hasText(form.getDescription())) {
      o.setDescription(form.getDescription());
    }
    if (StringUtils.hasText(form.getEmail())) {
      o.setEmail(form.getEmail());
    }
    if (StringUtils.hasText(form.getPhone())) {
      o.setPhone(form.getPhone());
    }
    if (StringUtils.hasText(form.getAddress())) {
      o.setAddress(form.getAddress());
    }
    if (StringUtils.hasText(form.getStatus())) {
      o.setStatus(form.getStatus());
    }

    if (form.getLogo() != null && !form.getLogo().isEmpty()) {
      String logoUrl = organizationHelper.uploadIfAny(form.getLogo());
      o.setLogoUrl(logoUrl);
    }

    o = organizationRepository.save(o);

    // publish UPDATED
    OrganizationPayload payload =
        organizationMapper.toOrganizationPayloadFromOrganization(o);
    eventProducer.publish(OrganizationEvent.builder()
        .type(OrganizationEvent.Type.UPDATED)
        .id(o.getId())
        .scopeType(ScopeType.Organization)
        .payload(payload)
        .build());

    return organizationMapper.toOrganizationResponseFromOrganization(o);
  }

  @Transactional
  public void deleteOrganization(String id) {
    String deletedBy = AuthenticationHelper.getMyUsername();
    Organization o = organizationRepository.findById(id)
        .orElseThrow(
            () -> new IllegalArgumentException("Organization not found"));
    o.markDeleted(deletedBy);
    organizationRepository.save(o);

    eventProducer.publish(OrganizationEvent.builder()
        .type(OrganizationEvent.Type.DELETED)
        .id(o.getId())
        .scopeType(ScopeType.Organization)
        .payload(null)
        .build());
  }

  public List<OrganizationResponse> getAllOrganizations() {
    return organizationRepository.findAll()
        .stream()
        .map(organizationMapper::toOrganizationResponseFromOrganization)
        .collect(Collectors.toList());
  }

  public OrganizationResponse getOrganizationById(String id) {
    return organizationRepository.findById(id)
        .map(organizationMapper::toOrganizationResponseFromOrganization)
        .orElseThrow(
            () -> new IllegalArgumentException("Organization not found"));
  }
}