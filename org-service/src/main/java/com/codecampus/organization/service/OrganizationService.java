package com.codecampus.organization.service;

import com.codecampus.organization.dto.common.PageResponse;
import com.codecampus.organization.dto.request.CreateOrganizationForm;
import com.codecampus.organization.dto.request.UpdateOrganizationForm;
import com.codecampus.organization.dto.response.BlockResponse;
import com.codecampus.organization.dto.response.OrganizationResponse;
import com.codecampus.organization.dto.response.OrganizationWithBlocksResponse;
import com.codecampus.organization.entity.Organization;
import com.codecampus.organization.exception.AppException;
import com.codecampus.organization.exception.ErrorCode;
import com.codecampus.organization.helper.AuthenticationHelper;
import com.codecampus.organization.helper.OrganizationHelper;
import com.codecampus.organization.helper.PageResponseHelper;
import com.codecampus.organization.mapper.OrganizationMapper;
import com.codecampus.organization.repository.OrganizationBlockRepository;
import com.codecampus.organization.repository.OrganizationRepository;
import com.codecampus.organization.service.kafka.OrganizationEventProducer;
import events.org.data.OrganizationPayload;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationService {
  OrganizationRepository organizationRepository;
  OrganizationBlockRepository blockRepository;
  BlockService blockService;
  OrganizationMapper organizationMapper;
  OrganizationEventProducer eventProducer;
  OrganizationHelper organizationHelper;

  @Transactional
  public void create(
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
    eventProducer.publishCreated(o.getId(), payload);
  }

  @Transactional
  public void update(
      String id,
      UpdateOrganizationForm form) {
    Organization o = organizationRepository.findById(id)
        .orElseThrow(
            () -> new AppException(ErrorCode.ORGANIZATION_NOT_FOUND));
    String logoUrl = organizationHelper.uploadIfAny(form.getLogo());

    organizationMapper
        .patchUpdateOrganizationFromUpdateOrganizationForm(form, o);
    o.setLogoUrl(logoUrl);

    o = organizationRepository.save(o);

    // publish UPDATED
    OrganizationPayload payload =
        organizationMapper.toOrganizationPayloadFromOrganization(o);
    eventProducer.publishUpdated(o.getId(), payload);
  }

  @Transactional
  public void delete(String id) {
    String deletedBy = AuthenticationHelper.getMyUsername();
    Organization o = organizationRepository.findById(id)
        .orElseThrow(
            () -> new AppException(ErrorCode.ORGANIZATION_NOT_FOUND));
    o.markDeleted(deletedBy);
    organizationRepository.save(o);

    eventProducer.publishDeleted(o.getId());
  }

  public OrganizationResponse get(String id) {
    return organizationRepository.findById(id)
        .map(organizationMapper::toOrganizationResponseFromOrganization)
        .orElseThrow(
            () -> new AppException(ErrorCode.ORGANIZATION_NOT_FOUND));
  }

  public PageResponse<OrganizationWithBlocksResponse> list(
      int orgPage, int orgSize,
      int blocksPage, int blocksSize) {

    Pageable orgPg = PageRequest.of(Math.max(orgPage - 1, 0), orgSize);
    Page<Organization> orgs = organizationRepository.findAll(orgPg);

    var out = orgs.map(o -> {
      // page blocks của từng org
      Pageable blkPg = PageRequest.of(Math.max(blocksPage - 1, 0), blocksSize,
          org.springframework.data.domain.Sort.by("createdAt").descending());
      var blockPage = blockRepository
          .findByOrgId(o.getId(), blkPg)
          .map(b -> BlockResponse.builder()
              .id(b.getId()).orgId(b.getOrgId())
              .name(b.getName()).code(b.getCode())
              .description(b.getDescription())
              .createdAt(b.getCreatedAt()).updatedAt(b.getUpdatedAt())
              .build());

      return OrganizationWithBlocksResponse.builder()
          .id(o.getId())
          .name(o.getName())
          .description(o.getDescription())
          .logoUrl(o.getLogoUrl())
          .email(o.getEmail())
          .phone(o.getPhone())
          .address(o.getAddress())
          .status(o.getStatus())
          .createdAt(o.getCreatedAt())
          .updatedAt(o.getUpdatedAt())
          .blocks(PageResponseHelper.toPageResponse(blockPage, blocksPage))
          .build();
    });

    return PageResponseHelper.toPageResponse(out, orgPage);
  }
}