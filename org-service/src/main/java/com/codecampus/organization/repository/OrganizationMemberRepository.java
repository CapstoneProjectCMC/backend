package com.codecampus.organization.repository;

import com.codecampus.constant.ScopeType;
import com.codecampus.organization.entity.OrganizationMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationMemberRepository
    extends JpaRepository<OrganizationMember, String> {
  Optional<OrganizationMember> findFirstByUserIdAndScopeTypeAndIsActiveIsTrueAndIsPrimaryIsTrueOrderByCreatedAtAsc(
      String userId, ScopeType scopeType);

  List<OrganizationMember> findByUserIdAndScopeTypeAndIsActiveIsTrue(
      String userId, ScopeType scopeType);

  List<OrganizationMember> findByScopeTypeAndScopeIdAndIsActiveIsTrue(
      ScopeType scopeType, String scopeId);

  Optional<OrganizationMember> findByUserIdAndScopeTypeAndScopeId(
      String userId,
      ScopeType scopeType,
      String scopeId);
}
