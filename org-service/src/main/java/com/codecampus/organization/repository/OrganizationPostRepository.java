package com.codecampus.organization.repository;


import com.codecampus.organization.entity.OrganizationPost;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrganizationPostRepository
    extends JpaRepository<OrganizationPost, String> {

  Page<OrganizationPost> findByOrgId(String orgId, Pageable pageable);

  @Query(value = """
      select * from organization_posts
      where post_id = :postId
      order by created_at desc limit 1
      """, nativeQuery = true)
  Optional<OrganizationPost> findAnyByPostId(@Param("postId") String postId);

  @Query(value = """
      select * from organization_posts
      where org_id = :orgId and post_id = :postId
      order by created_at desc limit 1
      """, nativeQuery = true)
  Optional<OrganizationPost> findAnyByOrgIdAndPostId(
      @Param("orgId") String orgId,
      @Param("postId") String postId);
}