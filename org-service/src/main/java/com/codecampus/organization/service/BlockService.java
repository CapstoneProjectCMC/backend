package com.codecampus.organization.service;

import com.codecampus.constant.ScopeType;
import com.codecampus.organization.dto.common.PageResponse;
import com.codecampus.organization.dto.request.BlockWithMembersPageResponse;
import com.codecampus.organization.dto.request.CreateBlockRequest;
import com.codecampus.organization.dto.request.UpdateBlockRequest;
import com.codecampus.organization.dto.response.MemberInBlockResponse;
import com.codecampus.organization.entity.OrganizationBlock;
import com.codecampus.organization.entity.OrganizationMember;
import com.codecampus.organization.exception.AppException;
import com.codecampus.organization.exception.ErrorCode;
import com.codecampus.organization.helper.AuthenticationHelper;
import com.codecampus.organization.helper.PageResponseHelper;
import com.codecampus.organization.mapper.BlockMapper;
import com.codecampus.organization.repository.OrganizationBlockRepository;
import com.codecampus.organization.repository.OrganizationMemberRepository;
import com.codecampus.organization.repository.OrganizationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlockService {
  OrganizationBlockRepository blockRepo;
  BlockMapper mapper;
  OrganizationRepository orgRepo;
  OrganizationMemberRepository memberRepo;

  @Transactional
  public void createBlock(
      String orgId,
      CreateBlockRequest req) {
    orgRepo.findById(orgId)
        .orElseThrow(() -> new AppException(ErrorCode.ORGANIZATION_NOT_FOUND));

    OrganizationBlock b = OrganizationBlock.builder()
        .orgId(orgId)
        .name(req.getName())
        .code(req.getCode())
        .description(req.getDescription())
        .build();
    blockRepo.save(b);
  }

  @Transactional
  public void updateBlock(
      String blockId,
      UpdateBlockRequest req) {
    OrganizationBlock b = blockRepo.findById(blockId)
        .orElseThrow(() -> new AppException(ErrorCode.GRADE_NOT_FOUND));

    mapper.patchUpdateOrganizationBlockFromUpdateBlockRequest(
        req, b);

    blockRepo.save(b);
  }

  @Transactional
  public void deleteBlock(String blockId) {
    String by = AuthenticationHelper.getMyUsername();
    OrganizationBlock b = blockRepo.findById(blockId)
        .orElseThrow(() -> new AppException(ErrorCode.GRADE_NOT_FOUND));
    b.markDeleted(by);
    blockRepo.save(b);
  }


  public PageResponse<BlockWithMembersPageResponse> getBlocksOfOrg(
      String orgId,
      int blocksPage, int blocksSize,
      int membersPage, int membersSize,
      boolean activeOnlyMembers,
      boolean includeUnassigned) {

    Pageable blocksPg = PageRequest.of(Math.max(blocksPage - 1, 0), blocksSize,
        Sort.by("createdAt").descending());
    Page<OrganizationBlock> blockPage = blockRepo.findByOrgId(orgId, blocksPg);

    var memberPg = PageRequest.of(Math.max(membersPage - 1, 0), membersSize,
        Sort.by("createdAt").descending());

    java.util.List<BlockWithMembersPageResponse> data =
        new java.util.ArrayList<>();

    for (OrganizationBlock b : blockPage.getContent()) {
      Page<OrganizationMember> memPage = activeOnlyMembers
          ?
          memberRepo.findByScopeTypeAndScopeIdAndIsActiveIsTrue(ScopeType.Grade,
              b.getId(), memberPg)
          : memberRepo.findByScopeTypeAndScopeId(ScopeType.Grade, b.getId(),
          memberPg);

      Page<MemberInBlockResponse> mapped = memPage.map(m ->
          MemberInBlockResponse.builder()
              .userId(m.getUserId()).role(m.getRole()).active(m.isActive())
              .build()
      );

      data.add(BlockWithMembersPageResponse.builder()
          .id(b.getId())
          .orgId(b.getOrgId())
          .name(b.getName())
          .code(b.getCode())
          .description(b.getDescription())
          .createdAt(b.getCreatedAt())
          .updatedAt(b.getUpdatedAt())
          .members(PageResponseHelper.toPageResponse(mapped, membersPage))
          .build());
    }

    // Append block ảo UNASSIGNED
    if (includeUnassigned) {
      java.util.List<String> blockIds = blockRepo.findBlockIdsOfOrg(orgId);
      Page<OrganizationMember> unassigned = activeOnlyMembers
          ?
          memberRepo.findUnassignedActiveMembersOfOrg(orgId, blockIds, memberPg)
          : memberRepo.findUnassignedMembersOfOrg(orgId, blockIds, memberPg);

      Page<MemberInBlockResponse> mapped = unassigned.map(m ->
          MemberInBlockResponse.builder()
              .userId(m.getUserId()).role(m.getRole()).active(m.isActive())
              .build()
      );

      data.add(BlockWithMembersPageResponse.builder()
          .id("virtual-unassigned-" + orgId)
          .orgId(orgId)
          .name("Unassigned")
          .code("UNASSIGNED")
          .description(
              "Members thuộc organization nhưng chưa nằm trong block nào")
          .members(PageResponseHelper.toPageResponse(mapped, membersPage))
          .build());
    }

    // Trả PageResponse cho blocks (không tính block ảo vào totalElements để không sai trang)
    return PageResponse.<BlockWithMembersPageResponse>builder()
        .currentPage(blocksPage)
        .pageSize(blockPage.getSize())
        .totalPages(blockPage.getTotalPages())
        .totalElements(blockPage.getTotalElements())
        .data(data)
        .build();
  }

  public BlockWithMembersPageResponse getBlock(
      String blockId,
      int memberPage, int memberSize,
      boolean activeOnly) {
    OrganizationBlock b = blockRepo.findById(blockId)
        .orElseThrow(() -> new AppException(ErrorCode.GRADE_NOT_FOUND));

    var pageable = PageRequest.of(Math.max(memberPage - 1, 0), memberSize,
        Sort.by("createdAt").descending());

    Page<OrganizationMember> memPage = activeOnly
        ? memberRepo.findByScopeTypeAndScopeIdAndIsActiveIsTrue(ScopeType.Grade,
        blockId, pageable)
        : memberRepo.findByScopeTypeAndScopeId(ScopeType.Grade, blockId,
        pageable);

    Page<MemberInBlockResponse> mapped = memPage.map(m ->
        MemberInBlockResponse.builder()
            .userId(m.getUserId()).role(m.getRole()).active(m.isActive())
            .build()
    );

    return BlockWithMembersPageResponse.builder()
        .id(b.getId())
        .orgId(b.getOrgId())
        .name(b.getName())
        .code(b.getCode())
        .description(b.getDescription())
        .createdAt(b.getCreatedAt())
        .updatedAt(b.getUpdatedAt())
        .members(PageResponseHelper.toPageResponse(mapped, memberPage))
        .build();
  }
}