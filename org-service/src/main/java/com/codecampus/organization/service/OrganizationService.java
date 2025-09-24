package com.codecampus.organization.service;

import com.codecampus.constant.ScopeType;
import com.codecampus.organization.dto.common.PageResponse;
import com.codecampus.organization.dto.request.CreateOrganizationForm;
import com.codecampus.organization.dto.request.UpdateOrganizationForm;
import com.codecampus.organization.dto.response.IdNameResponse;
import com.codecampus.organization.dto.response.OrganizationResponse;
import com.codecampus.organization.entity.Organization;
import com.codecampus.organization.exception.AppException;
import com.codecampus.organization.exception.ErrorCode;
import com.codecampus.organization.helper.AuthenticationHelper;
import com.codecampus.organization.helper.OrganizationHelper;
import com.codecampus.organization.helper.PageResponseHelper;
import com.codecampus.organization.mapper.OrganizationMapper;
import com.codecampus.organization.repository.OrganizationBlockRepository;
import com.codecampus.organization.repository.OrganizationMemberRepository;
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
  OrganizationMemberRepository memberRepository;
  MembershipService membershipService;
  BlockService blockService;
  OrganizationMapper organizationMapper;
  OrganizationEventProducer eventProducer;
  OrganizationHelper organizationHelper;

  @Transactional
  public void create(
      CreateOrganizationForm form) {

    String userId = AuthenticationHelper.getMyUserId();

    String logoUrl = organizationHelper.uploadIfAny(form.getLogo());

    Organization o = Organization.builder()
        .name(form.getName())
        .description(form.getDescription())
        .ownerId(userId)
        .logoUrl(logoUrl)
        .email(form.getEmail())
        .phone(form.getPhone())
        .address(form.getAddress())
        .status(form.getStatus())
        .build();

    o = organizationRepository.save(o);

    membershipService.addCreatorToOrg(userId, o.getId());

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
        .orElseThrow(() -> new AppException(ErrorCode.ORGANIZATION_NOT_FOUND));

    // Soft-delete organization
    o.markDeleted(deletedBy);
    organizationRepository.save(o);

    // 1) Soft-delete memberships cấp Organization
    var orgMembers = memberRepository.findByScopeTypeAndScopeId(
        ScopeType.Organization, id);
    for (var m : orgMembers) {
      m.setActive(false);
      m.setPrimary(false);
      m.markDeleted(deletedBy);
    }
    memberRepository.saveAll(orgMembers);

    // 2) Soft-delete memberships cấp Block thuộc org
    var blockIds = blockRepository.findBlockIdsOfOrg(id);
    for (String bid : blockIds) {
      var blockMembers = memberRepository.findByScopeTypeAndScopeId(
          ScopeType.Grade, bid);
      for (var m : blockMembers) {
        m.setActive(false);
        m.markDeleted(deletedBy);
      }
      memberRepository.saveAll(blockMembers);
    }

    // publish DELETED event như cũ
    eventProducer.publishDeleted(o.getId());
  }

  public PageResponse<OrganizationResponse> list(
      int page, int size) {
    Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size);
    Page<Organization> data = organizationRepository.findAll(pageable);
    Page<OrganizationResponse> mapped =
        data.map(organizationMapper::toOrganizationResponseFromOrganization);
    return PageResponseHelper.toPageResponse(mapped, page);
  }

  public OrganizationResponse get(String id) {
    return organizationRepository.findById(id)
        .map(organizationMapper::toOrganizationResponseFromOrganization)
        .orElseThrow(
            () -> new AppException(ErrorCode.ORGANIZATION_NOT_FOUND));
  }

  public IdNameResponse resolveOrganizationByName(String name) {
    Organization org = organizationRepository
        .findByName(name)
        .orElseThrow(
            () -> new AppException(ErrorCode.ORGANIZATION_NOT_FOUND));

    return IdNameResponse.builder()
        .id(org.getId())
        .name(org.getName())
        .build();
  }
}