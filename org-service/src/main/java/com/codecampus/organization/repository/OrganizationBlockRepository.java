package com.codecampus.organization.repository;

import com.codecampus.organization.entity.OrganizationBlock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationBlockRepository
    extends JpaRepository<OrganizationBlock, String> {
  List<OrganizationBlock> findByOrgId(String orgId);

  Page<OrganizationBlock> findByOrgId(
      String orgId, Pageable pageable);

  @Query("select b.id from OrganizationBlock b where b.orgId = :orgId")
  List<String> findBlockIdsOfOrg(@Param("orgId") String orgId);

  Optional<OrganizationBlock> findFirstByOrgIdAndNameIgnoreCase(
      String orgId,
      String name);

  List<OrganizationBlock> findByOrgIdAndNameIgnoreCase(
      String orgId,
      String name);

  Optional<OrganizationBlock> findFirstByOrgIdAndNameIgnoreCaseAndCodeIgnoreCase(
      String orgId, String name, String code);
}