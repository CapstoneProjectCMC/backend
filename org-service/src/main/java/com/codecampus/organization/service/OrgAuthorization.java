package com.codecampus.organization.service;

import com.codecampus.constant.ScopeType;
import com.codecampus.organization.entity.OrganizationBlock;
import com.codecampus.organization.entity.OrganizationMember;
import com.codecampus.organization.exception.AppException;
import com.codecampus.organization.exception.ErrorCode;
import com.codecampus.organization.helper.AuthenticationHelper;
import com.codecampus.organization.repository.OrganizationBlockRepository;
import com.codecampus.organization.repository.OrganizationMemberRepository;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrgAuthorization {

  private final OrganizationMemberRepository memberRepo;
  private final OrganizationBlockRepository blockRepo;

  public void ensureRoleAtLeastForOrg(String orgId, OrgRole minRole) {
    String me = AuthenticationHelper.getMyUserId();
    OrganizationMember m = memberRepo
        .findByUserIdAndScopeTypeAndScopeId(me, ScopeType.Organization, orgId)
        .orElseThrow(() -> new AppException(ErrorCode.MEMBERSHIP_NOT_FOUND));
    if (!m.isActive() || level(m.getRole()) < minRole.ordinal()) {
      throw new AppException(ErrorCode.UNAUTHORIZED);
    }
  }

  public void ensureRoleAtLeastForBlock(String blockId, OrgRole minRole) {
    OrganizationBlock b = blockRepo.findById(blockId)
        .orElseThrow(() -> new AppException(ErrorCode.GRADE_NOT_FOUND));
    ensureRoleAtLeastForOrg(b.getOrgId(), minRole);
  }

  public void ensureSelfOrMinRoleForOrg(
      String targetUserId, String orgId,
      OrgRole minRole) {
    String me = AuthenticationHelper.getMyUserId();
    if (me != null && me.equals(targetUserId)) {
      return; // self
    }
    ensureRoleAtLeastForOrg(orgId, minRole);
  }

  private int level(String role) {
    if (role == null) {
      return OrgRole.STUDENT.ordinal();
    }
    return switch (role.toUpperCase(Locale.ROOT)) {
      case "ADMIN" -> OrgRole.ADMIN.ordinal();
      case "TEACHER" -> OrgRole.TEACHER.ordinal();
      default -> OrgRole.STUDENT.ordinal();
    };
  }

  public enum OrgRole { STUDENT, TEACHER, ADMIN }

}
