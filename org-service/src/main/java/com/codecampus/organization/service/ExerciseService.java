package com.codecampus.organization.service;

import com.codecampus.organization.dto.common.PageResponse;
import com.codecampus.organization.entity.OrganizationExercise;
import com.codecampus.organization.exception.AppException;
import com.codecampus.organization.exception.ErrorCode;
import com.codecampus.organization.helper.AuthenticationHelper;
import com.codecampus.organization.helper.PageResponseHelper;
import com.codecampus.organization.mapper.ExerciseMapper;
import com.codecampus.organization.repository.OrganizationExerciseRepository;
import com.codecampus.organization.repository.OrganizationRepository;
import events.exercise.data.ExercisePayload;
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
public class ExerciseService {
  OrganizationRepository organizationRepository;
  OrganizationExerciseRepository orgExerciseRepo;
  ExerciseMapper mapper;

  @Transactional
  public void addOrUpdateFromExercisePayload(
      String exerciseId,
      ExercisePayload p) {
    if (p == null) {
      return;
    }

    String orgId = p.getOrgId();
    // Nếu exercise không thuộc org → xoá membership cũ (nếu có) và thoát
    if (orgId == null || orgId.isBlank()) {
      orgExerciseRepo.findAnyByExerciseId(exerciseId).ifPresent(oe -> {
        oe.markDeleted("system");
        orgExerciseRepo.save(oe);
      });
      return;
    }

    // bảo đảm org tồn tại
    organizationRepository.findById(orgId)
        .orElseThrow(() -> new AppException(ErrorCode.ORGANIZATION_NOT_FOUND));

    // Kiểm tra membership hiện tại (kể cả soft-deleted)
    var any = orgExerciseRepo.findAnyByExerciseId(exerciseId).orElse(null);

    // Nếu thay đổi org → đóng membership cũ và tạo/khôi phục membership mới
    if (any != null && !Objects.equals(any.getOrgId(), orgId)) {
      if (!any.isDeleted()) {
        any.markDeleted("system");
        orgExerciseRepo.save(any);
      }
      any = null; // để tạo mới/khôi phục cho orgId mới
    }

    OrganizationExercise target = any;
    if (target == null) {
      // thử tìm record (kể cả soft delete) đúng cặp org-exercise để khôi phục
      target = orgExerciseRepo
          .findAnyByOrgIdAndExerciseId(orgId, exerciseId)
          .orElse(OrganizationExercise.builder()
              .orgId(orgId)
              .exerciseId(exerciseId)
              .build());
    }

    // clear soft-delete nếu có
    target.setDeletedAt(null);
    target.setDeletedBy(null);

    // cập nhật snapshot
    target.setTitle(p.getTitle());
    target.setExerciseType(p.getExerciseType());
    target.setDifficulty(
        p.getDifficulty() == null ? null : String.valueOf(p.getDifficulty()));
    target.setVisibility(Boolean.TRUE.equals(p.getVisibility()));
    target.setFreeForOrg(Boolean.TRUE.equals(p.getFreeForOrg()));

    orgExerciseRepo.save(target);
  }

  @Transactional
  public void softDeleteByExerciseId(String exerciseId) {
    orgExerciseRepo.findAnyByExerciseId(exerciseId).ifPresent(oe -> {
      oe.markDeleted(AuthenticationHelper.getMyUsername());
      orgExerciseRepo.save(oe);
    });
  }

  @Transactional(readOnly = true)
  public PageResponse<OrganizationExercise> listExercisesOfOrg(
      String orgId, int page, int size) {
    // ensure org exists
    organizationRepository.findById(orgId)
        .orElseThrow(() -> new AppException(ErrorCode.ORGANIZATION_NOT_FOUND));

    Pageable pageable = PageRequest.of(Math.max(page - 1, 0), Math.max(size, 1),
        Sort.by(Sort.Order.desc("createdAt")));

    Page<OrganizationExercise> mapped = orgExerciseRepo
        .findByOrgId(orgId, pageable);

    return PageResponseHelper.toPageResponse(mapped, page);
  }
}
