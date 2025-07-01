package com.codecampus.profile.service;

import static com.codecampus.profile.utils.PageResponseUtils.toPageResponse;

import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.entity.properties.exercise.AssignedOrgExercise;
import com.codecampus.profile.entity.properties.organization.CreatedOrg;
import com.codecampus.profile.entity.properties.organization.MemberOrg;
import com.codecampus.profile.repository.OrgRepository;
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
public class OrganizationService
{
  UserProfileRepository userProfileRepository;
  OrgRepository orgRepository;

  // Admin Org
  public PageResponse<CreatedOrg> getMyCreatedOrgs(
      int page, int size)
  {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = userProfileRepository
        .findCreatedOrgs(SecurityUtils.getMyUserId(), pageable);

    return toPageResponse(pageData, page);
  }

  public PageResponse<MemberOrg> getMyMemberOrgs(
      int page, int size)
  {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = userProfileRepository
        .findMemberOrgs(SecurityUtils.getMyUserId(), pageable);

    return toPageResponse(pageData, page);
  }

  public PageResponse<MemberOrg> getMyTeacherOrgs(
      int page, int size)
  {
    Pageable p = PageRequest.of(page - 1, size);
    return toPageResponse(
        userProfileRepository.findMemberOrgsByRole(
            SecurityUtils.getMyUserId(), "TEACHER", p),
        page);
  }

  public PageResponse<MemberOrg> getMyAdminOrgs(
      int page, int size)
  {
    Pageable p = PageRequest.of(page - 1, size);
    return toPageResponse(
        userProfileRepository.findMemberOrgsByRole(
            SecurityUtils.getMyUserId(), "ADMIN", p),
        page);
  }

  // Class
  public PageResponse<AssignedOrgExercise> assignedExercisesOfOrg(
      String orgId, int page, int size)
  {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = orgRepository
        .findAssignedExercises(orgId, pageable);

    return toPageResponse(pageData, page);
  }
}
