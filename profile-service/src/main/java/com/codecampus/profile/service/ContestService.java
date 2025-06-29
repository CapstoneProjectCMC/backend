package com.codecampus.profile.service;

import static com.codecampus.profile.utils.PageResponseUtils.toPageResponse;

import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.entity.properties.contest.ContestStatus;
import com.codecampus.profile.repository.UserProfileRepository;
import com.codecampus.profile.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContestService
{
  UserProfileRepository userProfileRepository;

  public PageResponse<ContestStatus> myContestStatuses(int page, int size)
  {
    Pageable pageable = PageRequest.of(page - 1, size);
    var data = userProfileRepository.findContestStatuses(SecurityUtils.getMyUserId(), pageable);
    return toPageResponse(data, page);
  }
}
