package com.codecampus.organization.repository;

import com.codecampus.constant.ScopeType;
import com.codecampus.organization.entity.OrganizationMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

  List<OrganizationMember> findByScopeTypeAndScopeId(
      ScopeType scopeType, String scopeId);

  Optional<OrganizationMember> findByUserIdAndScopeTypeAndScopeId(
      String userId,
      ScopeType scopeType,
      String scopeId);

  Page<OrganizationMember> findByScopeTypeAndScopeId(
      ScopeType scopeType, String scopeId, Pageable pageable);

  Page<OrganizationMember> findByScopeTypeAndScopeIdAndIsActiveIsTrue(
      ScopeType scopeType, String scopeId, Pageable pageable);

  // Đếm số membership ACTIVE cấp Organization của user
  @Query("""
      select count(m) from OrganizationMember m, Organization o
       where m.userId = :userId
         and m.scopeType = com.codecampus.constant.ScopeType.Organization
         and m.isActive = true
         and o.id = m.scopeId
      """)
  long countActiveOrganizations(@Param("userId") String userId);

  // Lấy tất cả membership ACTIVE của user ở cấp Organization
  @Query("""
      select m from OrganizationMember m, Organization o
       where m.userId = :userId
         and m.scopeType = com.codecampus.constant.ScopeType.Organization
         and m.isActive = true
         and o.id = m.scopeId
      """)
  List<OrganizationMember> findActiveOrgsOfUser(@Param("userId") String userId);

  // Lấy tất cả member ACTIVE của 1 org
  @Query("select m from OrganizationMember m " +
      "where m.scopeType = com.codecampus.constant.ScopeType.Organization " +
      "and m.scopeId = :orgId and m.isActive = true")
  List<OrganizationMember> findActiveMembersOfOrg(@Param("orgId") String orgId);

  // Lấy tất cả member ACTIVE của block
  @Query("select m from OrganizationMember m " +
      "where m.scopeType = com.codecampus.constant.ScopeType.Grade " +
      "and m.scopeId = :blockId and m.isActive = true")
  List<OrganizationMember> findActiveMembersOfBlock(
      @Param("blockId") String blockId);

  // Unassigned ACTIVE members của org (không có membership ACTIVE ở bất kỳ block nào của org)
  @Query("""
      select m from OrganizationMember m
       where m.scopeType = com.codecampus.constant.ScopeType.Organization
         and m.scopeId   = :orgId
         and m.isActive  = true
         and not exists (
           select 1 from OrganizationMember b
            where b.userId = m.userId
              and b.scopeType = com.codecampus.constant.ScopeType.Grade
              and b.scopeId in :blockIds
              and b.isActive = true
         )
      """)
  Page<OrganizationMember> findUnassignedActiveMembersOfOrg(
      @Param("orgId") String orgId,
      @Param("blockIds") List<String> blockIds,
      Pageable pageable);


  // Unassigned members (không lọc active ở cấp org)
  @Query("""
      select m from OrganizationMember m
       where m.scopeType = com.codecampus.constant.ScopeType.Organization
         and m.scopeId   = :orgId
         and not exists (
           select 1 from OrganizationMember b
            where b.userId = m.userId
              and b.scopeType = com.codecampus.constant.ScopeType.Grade
              and b.scopeId in :blockIds
              and b.isActive = true
         )
      """)
  Page<OrganizationMember> findUnassignedMembersOfOrg(
      @Param("orgId") String orgId,
      @Param("blockIds") List<String> blockIds,
      Pageable pageable);

  @Query(value = """
      select * from organization_members 
      where user_id = :userId and scope_type = :scopeType and scope_id = :scopeId
      order by created_at desc limit 1
      """, nativeQuery = true)
  Optional<OrganizationMember> findAnyMembership(
      @Param("userId") String userId,
      @Param("scopeType") String scopeType,
      @Param("scopeId") String scopeId);
}
