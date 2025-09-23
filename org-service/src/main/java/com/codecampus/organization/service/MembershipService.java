package com.codecampus.organization.service;

import com.codecampus.constant.ScopeType;
import com.codecampus.organization.dto.common.PageResponse;
import com.codecampus.organization.dto.request.BulkAddMembersRequest;
import com.codecampus.organization.dto.response.BlocksOfUserWithMemberResponse;
import com.codecampus.organization.dto.response.ImportMembersResult;
import com.codecampus.organization.dto.response.MemberInBlockWithMemberResponse;
import com.codecampus.organization.dto.response.PrimaryOrgResponse;
import com.codecampus.organization.entity.OrganizationBlock;
import com.codecampus.organization.entity.OrganizationMember;
import com.codecampus.organization.exception.AppException;
import com.codecampus.organization.exception.ErrorCode;
import com.codecampus.organization.helper.AuthenticationHelper;
import com.codecampus.organization.helper.OrganizationMemberHelper;
import com.codecampus.organization.helper.PageResponseHelper;
import com.codecampus.organization.repository.OrganizationBlockRepository;
import com.codecampus.organization.repository.OrganizationMemberRepository;
import com.codecampus.organization.service.cache.UserBulkLoader;
import com.codecampus.organization.service.cache.UserSummaryCacheService;
import com.codecampus.organization.service.kafka.OrganizationMemberEventProducer;
import dtos.UserSummary;
import events.org.OrganizationMemberEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MembershipService {
  OrganizationMemberRepository memberRepository;
  OrganizationBlockRepository blockRepository;
  OrganizationMemberEventProducer eventProducer;
  OrganizationMemberHelper organizationMemberHelper;
  UserBulkLoader userBulkLoader;
  UserSummaryCacheService userSummaryCacheService;
  OrgAuthorization auth;

  @Transactional
  public void addToOrg(
      String userId,
      String orgId,
      String role,
      boolean active) {

    auth.ensureRoleAtLeastForOrg(orgId, OrgAuthorization.OrgRole.ADMIN);

    List<OrganizationMember> actives =
        memberRepository.findActiveOrgsOfUser(userId);
    boolean joinedOther =
        actives.stream().anyMatch(m -> !m.getScopeId().equals(orgId));
    if (active && joinedOther) {
      throw new AppException(ErrorCode.INVALID_REQUEST_MEMBER);
    }

    // Ưu tiên “hồi sinh” record cũ nếu có (kể cả đã soft-delete)
    var any = memberRepository.findAnyMembership(userId,
        ScopeType.Organization.name(), orgId);
    OrganizationMember member = any.orElse(OrganizationMember.builder()
        .userId(userId)
        .scopeType(ScopeType.Organization)
        .scopeId(orgId)
        .build());

    member.setRole(organizationMemberHelper.normalizeRole(role));
    member.setActive(active);

    // clear soft-delete nếu từng xóa
    member.setDeletedAt(null);
    member.setDeletedBy(null);

    // set primary nếu đây là membership org đầu tiên
    if (member.getId() == null && active && actives.isEmpty()) {
      member.setPrimary(true);
    }

    boolean isNew = member.getId() == null;
    member = memberRepository.save(member);
    organizationMemberHelper.publishEvent(member,
        isNew ? OrganizationMemberEvent.Type.ADDED :
            OrganizationMemberEvent.Type.UPDATED);
  }

  @Transactional
  public void addCreatorToOrg(String userId, String orgId, String role,
                              boolean active) {
    // KHÔNG gọi auth.ensureRoleAtLeastForOrg

    // Vẫn giữ rule: không được ở 2 org active cùng lúc
    List<OrganizationMember> actives =
        memberRepository.findActiveOrgsOfUser(userId);
    boolean joinedOther =
        actives.stream().anyMatch(m -> !m.getScopeId().equals(orgId));
    if (active && joinedOther) {
      throw new AppException(ErrorCode.INVALID_REQUEST_MEMBER);
    }

    var any = memberRepository.findAnyMembership(
        userId, ScopeType.Organization.name(), orgId);

    OrganizationMember member = any.orElse(OrganizationMember.builder()
        .userId(userId)
        .scopeType(ScopeType.Organization)
        .scopeId(orgId)
        .build());

    member.setRole(organizationMemberHelper.normalizeRole(role));
    member.setActive(active);

    // clear soft-delete nếu từng bị xoá
    member.setDeletedAt(null);
    member.setDeletedBy(null);

    // set primary nếu là org active đầu tiên
    if (member.getId() == null && active && actives.isEmpty()) {
      member.setPrimary(true);
    }

    boolean isNew = member.getId() == null;
    member = memberRepository.save(member);

    // publish event ADDED/UPDATED giống addToOrg
    organizationMemberHelper.publishEvent(
        member, isNew ? OrganizationMemberEvent.Type.ADDED :
            OrganizationMemberEvent.Type.UPDATED
    );
  }

  @Transactional
  public void addCreatorToOrg(String userId, String orgId) {
    addCreatorToOrg(userId, orgId, "Admin", true);
  }

  @Transactional
  public void addToBlock(
      String userId,
      String blockId,
      String role,
      boolean active) {

    auth.ensureRoleAtLeastForBlock(blockId, OrgAuthorization.OrgRole.TEACHER);

    // đảm bảo user đang ở trong org của block (nếu chưa -> auto join org với role Student)
    String orgId = blockRepository.findById(blockId)
        .orElseThrow(() -> new AppException(ErrorCode.GRADE_NOT_FOUND))
        .getOrgId();

    if (memberRepository.findActiveOrgsOfUser(userId).stream()
        .noneMatch(m -> m.getScopeId().equals(orgId))) {
      addToOrg(userId, orgId,
          role != null ? role : "Student",
          true);
    }

    var any = memberRepository
        .findAnyMembership(userId, ScopeType.Grade.name(), blockId);
    OrganizationMember m = any
        .orElse(OrganizationMember.builder()
            .userId(userId)
            .scopeType(ScopeType.Grade)
            .scopeId(blockId)
            .build());
    m.setRole(organizationMemberHelper.normalizeRole(role));
    m.setActive(active);

    // clear soft-delete nếu từng xóa
    m.setDeletedAt(null);
    m.setDeletedBy(null);

    boolean isNew = m.getId() == null;
    m = memberRepository.save(m);
    organizationMemberHelper.publishEvent(m,
        isNew ? OrganizationMemberEvent.Type.ADDED :
            OrganizationMemberEvent.Type.UPDATED);
  }

  @Transactional
  public void removeMembership(
      String userId,
      ScopeType scopeType,
      String scopeId) {

    if (scopeType == ScopeType.Organization) {
      auth.ensureSelfOrMinRoleForOrg(userId, scopeId,
          OrgAuthorization.OrgRole.ADMIN); // self hoặc Admin org
    } else if (scopeType == ScopeType.Grade) {
      var orgId = blockRepository.findById(scopeId)
          .orElseThrow(() -> new AppException(ErrorCode.GRADE_NOT_FOUND))
          .getOrgId();
      auth.ensureSelfOrMinRoleForOrg(userId, orgId,
          OrgAuthorization.OrgRole.TEACHER); // self hoặc Admin/Teacher
    }

    String by = AuthenticationHelper.getMyUsername();
    OrganizationMember member = memberRepository
        .findByUserIdAndScopeTypeAndScopeId(userId, scopeType, scopeId)
        .orElseThrow(
            () -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND));

    member.markDeleted(by);
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

  public PageResponse<MemberInBlockWithMemberResponse> listOrgMembers(
      String orgId, int page, int size, boolean activeOnly) {
    Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size,
        Sort.by("createdAt").descending());
    Page<OrganizationMember> data = activeOnly
        ? memberRepository.findByScopeTypeAndScopeIdAndIsActiveIsTrue(
        ScopeType.Organization, orgId, pageable)
        : memberRepository.findByScopeTypeAndScopeId(ScopeType.Organization,
        orgId,
        pageable);

    Set<String> uids = data.getContent().stream()
        .map(OrganizationMember::getUserId)
        .collect(Collectors.toSet());
    Map<String, UserSummary> summaries = userBulkLoader.loadAll(uids);

    Page<MemberInBlockWithMemberResponse> mapped = data.map(m ->
        MemberInBlockWithMemberResponse.builder()
            .user(summaries.get(m.getUserId()))
            .role(m.getRole())
            .active(m.isActive())
            .build()
    );

    return PageResponseHelper.toPageResponse(mapped, page);
  }

  @Transactional
  public void switchBlock(
      String userId, String fromBlockId, String toBlockId, String role) {

    OrganizationBlock from = blockRepository.findById(fromBlockId)
        .orElseThrow(() -> new AppException(ErrorCode.GRADE_NOT_FOUND));
    OrganizationBlock to = blockRepository.findById(toBlockId)
        .orElseThrow(() -> new AppException(ErrorCode.GRADE_NOT_FOUND));

    if (!Objects.equals(from.getOrgId(), to.getOrgId())) {
      throw new AppException(
          ErrorCode.INVALID_REQUEST_MEMBER); // khác org -> từ chối
    }

    // 1) Tắt membership ở block cũ (nếu có)
    Optional<OrganizationMember> old = memberRepository
        .findByUserIdAndScopeTypeAndScopeId(userId, ScopeType.Grade,
            fromBlockId);
    old.ifPresent(m -> {
      m.setActive(false);
      m.markDeleted("system");
      memberRepository.save(m);
      organizationMemberHelper.publishEvent(m,
          OrganizationMemberEvent.Type.DELETED);
    });

    // 2) Đảm bảo đang là member của ORGANIZATION
    Optional<OrganizationMember> orgMem = memberRepository
        .findByUserIdAndScopeTypeAndScopeId(userId, ScopeType.Organization,
            from.getOrgId());
    if (orgMem.isEmpty()) {
      // auto-join org với role Student nếu chưa có
      OrganizationMember join = OrganizationMember.builder()
          .userId(userId)
          .scopeType(ScopeType.Organization)
          .scopeId(from.getOrgId())
          .role(role != null ? role : "Student")
          .isActive(true)
          .build();
      memberRepository.save(join);
      organizationMemberHelper.publishEvent(join,
          OrganizationMemberEvent.Type.ADDED);
    }

    // 3) Add/Update membership ở block mới
    OrganizationMember toMem = memberRepository
        .findByUserIdAndScopeTypeAndScopeId(userId, ScopeType.Grade, toBlockId)
        .orElse(OrganizationMember.builder()
            .userId(userId)
            .scopeType(ScopeType.Grade)
            .scopeId(toBlockId)
            .build());
    toMem.setRole(role != null ? organizationMemberHelper.normalizeRole(role) :
        (toMem.getRole() == null ? "Student" : toMem.getRole()));
    toMem.setActive(true);
    boolean isNew = toMem.getId() == null;
    memberRepository.save(toMem);
    organizationMemberHelper.publishEvent(toMem,
        isNew ? OrganizationMemberEvent.Type.ADDED :
            OrganizationMemberEvent.Type.UPDATED);
  }

  @Transactional
  public void leaveOrganization(String userId, String orgId) {
    // Disable org membership
    OrganizationMember orgMem = memberRepository
        .findByUserIdAndScopeTypeAndScopeId(userId, ScopeType.Organization,
            orgId)
        .orElseThrow(
            () -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND));
    orgMem.setActive(false);
    orgMem.setPrimary(false);
    orgMem.markDeleted("system"); // soft delete
    memberRepository.save(orgMem);
    organizationMemberHelper.publishEvent(orgMem,
        OrganizationMemberEvent.Type.DELETED);
  }

  @Transactional
  public void leaveOrg(String orgId) {

    String userId = AuthenticationHelper.getMyUserId();

    // rời org
    leaveOrganization(userId, orgId);

    // rời tất cả block thuộc org
    List<String> blockIds = blockRepository.findBlockIdsOfOrg(orgId);
    for (String bid : blockIds) {
      memberRepository.findByUserIdAndScopeTypeAndScopeId(userId,
              ScopeType.Grade, bid)
          .ifPresent(m -> {
            m.setActive(false);
            m.markDeleted("system");
            memberRepository.save(m);
            organizationMemberHelper.publishEvent(m,
                OrganizationMemberEvent.Type.DELETED);
          });
    }
  }

  @Transactional
  public void leaveBlock(String blockId) {

    String userId = AuthenticationHelper.getMyUserId();

    OrganizationMember m = memberRepository
        .findByUserIdAndScopeTypeAndScopeId(userId, ScopeType.Grade, blockId)
        .orElseThrow(
            () -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND));
    m.setActive(false);
    m.markDeleted("system");
    memberRepository.save(m);
    organizationMemberHelper.publishEvent(m,
        OrganizationMemberEvent.Type.DELETED);
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
          OrganizationMember m = list.getFirst();
          return PrimaryOrgResponse.builder()
              .organizationId(m.getScopeId())
              .role(m.getRole())
              .build();
        });
  }

  public PageResponse<MemberInBlockWithMemberResponse> listUnassignedMembers(
      String orgId, int page, int size, boolean activeOnly) {
    var pageable = PageRequest.of(Math.max(page - 1, 0), size,
        Sort.by("createdAt").descending());
    List<String> blockIds = blockRepository.findBlockIdsOfOrg(orgId);

    Page<OrganizationMember> p = activeOnly
        ? memberRepository.findUnassignedActiveMembersOfOrg(orgId, blockIds,
        pageable)
        :
        memberRepository.findUnassignedMembersOfOrg(orgId, blockIds, pageable);


    Set<String> uids = p.getContent().stream()
        .map(OrganizationMember::getUserId)
        .collect(Collectors.toSet());
    Map<String, UserSummary> summaries = userBulkLoader.loadAll(uids);

    Page<MemberInBlockWithMemberResponse> mapped = p.map(m ->
        MemberInBlockWithMemberResponse.builder()
            .user(summaries.get(m.getUserId()))
            .role(m.getRole())
            .active(m.isActive())
            .build()
    );

    return PageResponseHelper.toPageResponse(mapped, page);
  }

  @Transactional
  public ImportMembersResult importMembersToOrg(
      String orgId,
      MultipartFile file) {

    auth.ensureRoleAtLeastForOrg(orgId, OrgAuthorization.OrgRole.ADMIN);

    int total = 0, added = 0, skipped = 0;
    List<String> errors = new ArrayList<>();

    try (Workbook wb = new XSSFWorkbook(file.getInputStream())) {
      Sheet sheet = wb.getSheetAt(0);
      // Định dạng file:
      // cột A=userId,
      // B=role(Admin|Teacher|Student),
      // C=active(true|false)
      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        total++;
        Row r = sheet.getRow(i);
        if (r == null) {
          skipped++;
          continue;
        }
        String userId = organizationMemberHelper.getString(r, 0);
        String role = organizationMemberHelper.getString(r, 1);
        Boolean active = organizationMemberHelper.parseBoolean(
            organizationMemberHelper.getString(r, 2), true);

        if (userId.isBlank()) {
          skipped++;
          errors.add("Row " + (i + 1) + ": missing userId");
          continue;
        }
        try {
          addToOrg(userId, orgId, role, active);
          added++;
        } catch (Exception ex) {
          skipped++;
          errors.add("Row " + (i + 1) + ": " + ex.getMessage());
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return new ImportMembersResult(total, added, skipped, errors);
  }

  @Transactional
  public ImportMembersResult importMembersToBlock(
      String blockId,
      MultipartFile file) {

    auth.ensureRoleAtLeastForBlock(blockId, OrgAuthorization.OrgRole.TEACHER);

    int total = 0, added = 0, skipped = 0;
    List<String> errors = new ArrayList<>();

    try (Workbook wb = new XSSFWorkbook(file.getInputStream())) {
      Sheet sheet = wb.getSheetAt(0);
      // cột
      // A=userId,
      // B=role,
      // C=active
      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        total++;
        Row r = sheet.getRow(i);
        if (r == null) {
          skipped++;
          continue;
        }
        String userId = organizationMemberHelper.getString(r, 0);
        String role = organizationMemberHelper.getString(r, 1);
        Boolean active = organizationMemberHelper.parseBoolean(
            organizationMemberHelper.getString(r, 2), true);

        if (userId.isBlank()) {
          skipped++;
          errors.add("Row " + (i + 1) + ": missing userId");
          continue;
        }
        try {
          addToBlock(userId, blockId, role, active);
          added++;
        } catch (Exception ex) {
          skipped++;
          errors.add("Row " + (i + 1) + ": " + ex.getMessage());
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return new ImportMembersResult(total, added, skipped, errors);
  }

  @Transactional
  public void bulkAddToOrg(String orgId, BulkAddMembersRequest req) {

    auth.ensureRoleAtLeastForOrg(orgId, OrgAuthorization.OrgRole.ADMIN);

    for (var it : req.getMembers()) {
      String role = it.getRole() != null ? it.getRole() : req.getDefaultRole();
      boolean active = it.getActive() != null ? it.getActive() : req.isActive();
      addToOrg(it.getUserId(), orgId, role, active);
    }
  }

  @Transactional
  public void bulkAddToBlock(String blockId, BulkAddMembersRequest req) {

    auth.ensureRoleAtLeastForBlock(blockId, OrgAuthorization.OrgRole.TEACHER);

    for (var it : req.getMembers()) {
      String role = it.getRole() != null ? it.getRole() : req.getDefaultRole();
      boolean active = it.getActive() != null ? it.getActive() : req.isActive();
      addToBlock(it.getUserId(), blockId, role, active);
    }
  }

  /**
   * Danh sách block đang ACTIVE của 1 user
   */
  public BlocksOfUserWithMemberResponse listActiveBlocksOfUser(
      String userId) {
    var list = memberRepository
        .findByUserIdAndScopeTypeAndIsActiveIsTrue(userId, ScopeType.Grade);
    return BlocksOfUserWithMemberResponse.builder()
        .user(userSummaryCacheService.getOrLoad(userId))
        .blockIds(list.stream().map(OrganizationMember::getScopeId).toList())
        .build();
  }
}
