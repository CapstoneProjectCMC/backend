package com.codecampus.profile.service;

import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.entity.properties.subcribe.SubscribedTo;
import com.codecampus.profile.helper.SecurityHelper;
import com.codecampus.profile.repository.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.codecampus.profile.helper.PageResponseHelper.toPageResponse;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PackageService {
    UserProfileRepository userProfileRepository;

    public PageResponse<SubscribedTo> getMySubscriptions(
            int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        var pageData = userProfileRepository
                .findSubscriptions(SecurityHelper.getMyUserId(), pageable);

        return toPageResponse(pageData, page);
    }
}
