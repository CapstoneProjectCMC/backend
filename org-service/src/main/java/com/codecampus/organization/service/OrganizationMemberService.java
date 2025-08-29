package com.codecampus.organization.service;

import com.codecampus.constant.ScopeType;
import com.codecampus.organization.dto.request.CreateOrganizationMemberRequest;
import com.codecampus.organization.dto.response.PrimaryOrgResponse;
import com.codecampus.organization.entity.OrganizationMember;
import com.codecampus.organization.helper.OrganizationMemberHelper;
import com.codecampus.organization.repository.OrganizationMemberRepository;
import com.codecampus.organization.service.kafka.OrganizationMemberEventProducer;
import events.org.OrganizationMemberEvent;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationMemberService {
  OrganizationMemberRepository memberRepository;
  OrganizationMemberEventProducer eventProducer;

  @Transactional
  public void addMembership(CreateOrganizationMemberRequest req) {

    ScopeType scopeType = ScopeType.valueOf(req.getScopeType());

    // upsert nếu đã tồn tại membership
    OrganizationMember member = memberRepository
        .findByUserIdAndScopeTypeAndScopeId(req.getUserId(), scopeType,
            req.getScopeId())
        .orElse(OrganizationMember.builder()
            .userId(req.getUserId())
            .scopeType(scopeType)
            .scopeId(req.getScopeId())
            .build());

    member.setRole(OrganizationMemberHelper.normalizeRole(req.getRole()));
    member.setActive(req.isActive());

    boolean isNew = member.getId() == null;

    // Nếu là Organization và user chưa có primary thì set primary cho membership đầu tiên
    if (scopeType == ScopeType.Organization && isNew) {
      boolean alreadyPrimary = memberRepository
          .findFirstByUserIdAndScopeTypeAndIsActiveIsTrueAndIsPrimaryIsTrueOrderByCreatedAtAsc(
              req.getUserId(), ScopeType.Organization
          ).isPresent();
      if (!alreadyPrimary) {
        member.setPrimary(true);
      }
    }

    member = memberRepository.save(member);

    eventProducer.publish(OrganizationMemberEvent.builder()
        .type(isNew ? OrganizationMemberEvent.Type.ADDED :
            OrganizationMemberEvent.Type.UPDATED)
        .userId(member.getUserId())
        .scopeType(member.getScopeType())
        .scopeId(member.getScopeId())
        .role(member.getRole())
        .isActive(member.isActive())
        .at(Instant.now())
        .build());
  }

  @Transactional
  public void bulkAddMemberships(
      List<CreateOrganizationMemberRequest> list) {
    if (list == null || list.isEmpty()) {
      return;
    }
    // đơn giản: lặp và gọi addMembership; có thể tối ưu batch nếu cần
    list.forEach(this::addMembership);
  }

  @Transactional
  public void removeMembership(
      String userId,
      ScopeType scopeType,
      String scopeId) {
    OrganizationMember member = memberRepository
        .findByUserIdAndScopeTypeAndScopeId(userId, scopeType, scopeId)
        .orElseThrow(
            () -> new IllegalArgumentException("Membership not found"));

    member.markDeleted("system");
    memberRepository.save(member);

    eventProducer.publish(OrganizationMemberEvent.builder()
        .type(OrganizationMemberEvent.Type.DELETED)
        .userId(member.getUserId())
        .scopeType(member.getScopeType())
        .scopeId(member.getScopeId())
        .role(member.getRole())
        .isActive(false)
        .at(Instant.now())
        .build());
  }

  public PrimaryOrgResponse getPrimaryOrg(String userId) {
    return memberRepository
        .findFirstByUserIdAndScopeTypeAndIsActiveIsTrueAndIsPrimaryIsTrueOrderByCreatedAtAsc(
            userId, ScopeType.Organization)
        .map(m -> PrimaryOrgResponse.builder()
            .organizationId(m.getScopeId())
            .role(m.getRole())
            .build())
        .orElseGet(() -> {
          // fallback: lấy org active đầu tiên nếu chưa có primary
          List<OrganizationMember> list = memberRepository
              .findByUserIdAndScopeTypeAndIsActiveIsTrue(userId,
                  ScopeType.Organization);
          if (list.isEmpty()) {
            return null;
          }
          OrganizationMember m = list.get(0);
          return PrimaryOrgResponse.builder()
              .organizationId(m.getScopeId())
              .role(m.getRole())
              .build();
        });
  }

  @Transactional
  public void setPrimaryOrg(String userId, String orgId) {
    // bỏ primary cũ
    memberRepository.findByUserIdAndScopeTypeAndIsActiveIsTrue(userId,
            ScopeType.Organization)
        .forEach(m -> {
          if (m.isPrimary() &&
              !m.getScopeId().equals(orgId)) {
            m.setPrimary(false);
            memberRepository.save(m);
          }
        });

    OrganizationMember target = memberRepository
        .findByUserIdAndScopeTypeAndScopeId(userId, ScopeType.Organization,
            orgId)
        .orElseThrow(
            () -> new IllegalArgumentException("Membership not found"));

    target.setPrimary(true);
    memberRepository.save(target);
  }
}
