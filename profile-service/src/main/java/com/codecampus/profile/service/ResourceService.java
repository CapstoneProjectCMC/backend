package com.codecampus.profile.service;

import com.codecampus.profile.constant.type.ResourceType;
import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.entity.FileResource;
import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.entity.properties.resource.ReportedResource;
import com.codecampus.profile.entity.properties.resource.SavedResource;
import com.codecampus.profile.exception.AppException;
import com.codecampus.profile.exception.ErrorCode;
import com.codecampus.profile.helper.AuthenticationHelper;
import com.codecampus.profile.repository.FileResourceRepository;
import com.codecampus.profile.repository.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.codecampus.profile.helper.PageResponseHelper.toPageResponse;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ResourceService {
    FileResourceRepository fileResourceRepository;
    UserProfileRepository userProfileRepository;

    UserProfileService userProfileService;

    public void saveResource(String fileId) {
        UserProfile userProfile = userProfileService.getUserProfile();
        FileResource fileResource = getResource(fileId);
        userProfile.getSavedResources().add(
                SavedResource.builder().saveAt(Instant.now())
                        .resource(fileResource).build()
        );
        userProfileRepository.save(userProfile);
    }

    public void reportResource(
            String fileId, String reason) {
        UserProfile userProfile = userProfileService.getUserProfile();
        FileResource fileResource = getResource(fileId);
        userProfile.getReportedResources().add(
                ReportedResource.builder()
                        .reason(reason)
                        .reportedAt(Instant.now()).resource(fileResource)
                        .build()
        );
        userProfileRepository.save(userProfile);
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
