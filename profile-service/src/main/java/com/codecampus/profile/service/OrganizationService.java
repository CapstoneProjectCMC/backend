package com.codecampus.profile.service;

import static com.codecampus.profile.utils.PageResponseUtils.toPageResponse;

import com.codecampus.profile.dto.common.PageResponse;
import com.codecampus.profile.entity.Classroom;
import com.codecampus.profile.entity.Org;
import com.codecampus.profile.entity.properties.exercise.AssignedClassExercise;
import com.codecampus.profile.entity.properties.exercise.AssignedOrgExercise;
import com.codecampus.profile.entity.properties.organization.CreatedOrg;
import com.codecampus.profile.entity.properties.organization.EnrolledClass;
import com.codecampus.profile.entity.properties.organization.ManagesClass;
import com.codecampus.profile.entity.properties.organization.MemberOrg;
import com.codecampus.profile.exception.AppException;
import com.codecampus.profile.exception.ErrorCode;
import com.codecampus.profile.repository.ClassroomRepository;
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
public class OrganizationService {
  UserProfileRepository userProfileRepository;
  OrgRepository orgRepository;
  private final ClassroomRepository classroomRepository;

  // Admin Org
  public PageResponse<CreatedOrg> getMyCreatedOrgs(
      int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = userProfileRepository
        .findCreatedOrgs(SecurityUtils.getMyUserId(), pageable);

    return toPageResponse(pageData, page);
  }

  public PageResponse<MemberOrg> getMyMemberOrgs(
      int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = userProfileRepository
        .findMemberOrgs(SecurityUtils.getMyUserId(), pageable);

    return toPageResponse(pageData, page);
  }

  // Class
  // Teacher
  public PageResponse<ManagesClass> getMyManagedClasses(
      int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = userProfileRepository
        .findManagedClasses(SecurityUtils.getMyUserId(), pageable);

    return toPageResponse(pageData, page);
  }

  public PageResponse<EnrolledClass> getMyEnrolledClasses(
      int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = userProfileRepository
        .findEnrolledClasses(SecurityUtils.getMyUserId(), pageable);
    return toPageResponse(pageData, page);
  }

  public PageResponse<Classroom> classesOfOrg(String orgId, int page,
                                              int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = orgRepository
        .findClassesOfOrg(orgId, pageable);

    return toPageResponse(pageData, page);
  }

  public PageResponse<AssignedOrgExercise> assignedExercisesOfOrg(
      String orgId, int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = orgRepository
        .findAssignedExercises(orgId, pageable);

    return toPageResponse(pageData, page);
  }

  public PageResponse<AssignedClassExercise> assignedExercisesOfClass(
      String classId, int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);
    var pageData = classroomRepository
        .findAssignedExercises(classId, pageable);

    return toPageResponse(pageData, page);
  }

  public Org getOrgOfClass(String classId) {
    return classroomRepository
        .findOrgOfClass(classId)
        .orElseThrow(
            () -> new AppException(ErrorCode.ORG_NOT_FOUND)
        );
  }
}
