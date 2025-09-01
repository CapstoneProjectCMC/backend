package com.codecampus.organization.service;

import com.codecampus.organization.dto.common.PageResponse;
import com.codecampus.organization.entity.OrganizationPost;
import com.codecampus.organization.exception.AppException;
import com.codecampus.organization.exception.ErrorCode;
import com.codecampus.organization.helper.PageResponseHelper;
import com.codecampus.organization.mapper.PostMapper;
import com.codecampus.organization.repository.OrganizationPostRepository;
import com.codecampus.organization.repository.OrganizationRepository;
import dtos.PostSummary;
import events.post.data.PostPayload;
import java.util.Objects;
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
public class PostService {
  OrganizationRepository organizationRepository;
  OrganizationPostRepository orgPostRepo;
  PostMapper mapper;

  @Transactional
  public void addOrUpdateFromPostPayload(String postId, PostPayload p) {
    if (p == null) {
      return;
    }

    String orgId = p.getOrgId();
    // nếu post không thuộc org → xoá membership cũ (nếu có)
    if (orgId == null || orgId.isBlank()) {
      orgPostRepo.findAnyByPostId(postId).ifPresent(op -> {
        op.markDeleted("system");
        orgPostRepo.save(op);
      });
      return;
    }

    organizationRepository.findById(orgId)
        .orElseThrow(() -> new AppException(ErrorCode.ORGANIZATION_NOT_FOUND));

    var any = orgPostRepo.findAnyByPostId(postId).orElse(null);

    // đổi org → đóng record cũ
    if (any != null && !Objects.equals(any.getOrgId(), orgId)) {
      if (!any.isDeleted()) {
        any.markDeleted("system");
        orgPostRepo.save(any);
      }
      any = null;
    }

    OrganizationPost target = any;
    if (target == null) {
      target = orgPostRepo.findAnyByOrgIdAndPostId(orgId, postId)
          .orElse(OrganizationPost.builder()
              .orgId(orgId)
              .postId(postId)
              .build());
    }

    // clear soft-delete nếu có
    target.setDeletedAt(null);
    target.setDeletedBy(null);

    // snapshot
    target.setTitle(p.getTitle());
    target.setPostType(p.getPostType());
    target.setPublic(Boolean.TRUE.equals(p.getIsPublic()));

    orgPostRepo.save(target);
  }

  @Transactional
  public void softDeleteByPostId(String postId) {
    orgPostRepo.findAnyByPostId(postId).ifPresent(op -> {
      op.markDeleted("system");
      orgPostRepo.save(op);
    });
  }

  @Transactional(readOnly = true)
  public PageResponse<PostSummary> listPostsOfOrg(
      String orgId,
      int page, int size) {
    organizationRepository.findById(orgId)
        .orElseThrow(() -> new AppException(ErrorCode.ORGANIZATION_NOT_FOUND));

    Pageable pageable = PageRequest.of(Math.max(page - 1, 0), Math.max(size, 1),
        Sort.by(Sort.Order.desc("createdAt")));

    Page<PostSummary> mapped =
        orgPostRepo.findByOrgId(orgId, pageable).map(mapper::toPostSummary);

    return PageResponseHelper.toPageResponse(mapped, page);
  }
}