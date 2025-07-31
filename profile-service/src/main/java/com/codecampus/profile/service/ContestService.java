package com.codecampus.profile.service;

import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.entity.properties.contest.ContestStatus;
import com.codecampus.profile.helper.AuthenticationHelper;
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
public class ContestService {
    UserProfileRepository userProfileRepository;


    // TODO Khi nào thêm đồng bộ từ submission service qua, khi có các contest assign cho student
    public PageResponse<ContestStatus> getMyContestStatuses(int page,
                                                            int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        var data = userProfileRepository.findContestStatuses(
                AuthenticationHelper.getMyUserId(), pageable);
        return toPageResponse(data, page);
    }
}
