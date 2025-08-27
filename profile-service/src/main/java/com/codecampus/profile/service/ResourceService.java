package com.codecampus.profile.service;

import static com.codecampus.profile.helper.PageResponseHelper.toPageResponse;

import com.codecampus.profile.constant.type.ResourceType;
import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.entity.FileResource;
import com.codecampus.profile.entity.properties.resource.SavedResource;
import com.codecampus.profile.exception.AppException;
import com.codecampus.profile.exception.ErrorCode;
import com.codecampus.profile.helper.AuthenticationHelper;
import com.codecampus.profile.repository.FileResourceRepository;
import com.codecampus.profile.repository.UserProfileRepository;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResourceService {
  FileResourceRepository fileResourceRepository;
  UserProfileRepository userProfileRepository;
  UserProfileService userProfileService;

  @Transactional
  public void saveResource(String fileId) {
    getResource(fileId);
    userProfileRepository.mergeSavedResource(
        AuthenticationHelper.getMyUserId(), fileId, Instant.now());
  }

  @Transactional
  public void reportResource(
      String fileId, String reason) {
    getResource(fileId);
    userProfileRepository.mergeReportedResource(
        AuthenticationHelper.getMyUserId(), fileId, reason, Instant.now());
  }

  @Transactional
  public void unsaveResource(String fileId) {
    userProfileRepository.deleteSavedResource(
        AuthenticationHelper.getMyUserId(), fileId);
  }

  @Transactional
  public void unReportResource(String fileId) {
    userProfileRepository.deleteReportedResource(
        AuthenticationHelper.getMyUserId(), fileId);
  }

  public PageResponse<SavedResource> getSavedLectures(
      int page, int size) {
    return getSavedByType(ResourceType.LECTURE, page, size);
  }

  public PageResponse<SavedResource> getSavedTextbooks(
      int page, int size) {
    return getSavedByType(ResourceType.TEXTBOOK, page, size);
  }

  /* helper */
  private PageResponse<SavedResource> getSavedByType(
      ResourceType type, int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var data = userProfileRepository.findSavedResourcesByType(
        AuthenticationHelper.getMyUserId(), type.name(), pageable);
    return toPageResponse(data, page);
  }

  public FileResource getResource(String fileId) {
    return fileResourceRepository.findByFileId(fileId)
        .orElseThrow(
            () -> new AppException(ErrorCode.FILE_NOT_FOUND)
        );
  }
}
