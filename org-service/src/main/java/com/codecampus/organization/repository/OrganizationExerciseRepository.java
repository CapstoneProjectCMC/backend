package com.codecampus.organization.repository;

import com.codecampus.organization.entity.OrganizationExercise;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrganizationExerciseRepository
    extends JpaRepository<OrganizationExercise, String> {

  Page<OrganizationExercise> findByOrgId(
      String orgId,
      Pageable pageable);

  @Query(value = """
      select * from organization_exercises 
      where exercise_id = :exerciseId
      order by created_at desc limit 1
      """, nativeQuery = true)
  Optional<OrganizationExercise> findAnyByExerciseId(
      @Param("exerciseId") String exerciseId);

  @Query(value = """
      select * from organization_exercises 
      where org_id = :orgId and exercise_id = :exerciseId
      order by created_at desc limit 1
      """, nativeQuery = true)
  Optional<OrganizationExercise> findAnyByOrgIdAndExerciseId(
      @Param("orgId") String orgId,
      @Param("exerciseId") String exerciseId);
}