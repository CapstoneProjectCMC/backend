package com.codecampus.organization.entity;

import com.codecampus.organization.entity.audit.AuditMetadata;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "organization_posts",
    indexes = {
        @Index(name = "idx_org_posts_org", columnList = "org_id"),
        @Index(name = "idx_org_posts_post", columnList = "post_id")
    }
)
@SQLDelete(sql = "UPDATE organization_posts SET deleted_by = 'system', deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class OrganizationPost extends AuditMetadata {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @Column(name = "org_id", nullable = false, length = 36)
  String orgId;

  @Column(name = "post_id", nullable = false, length = 36)
  String postId;

  // snapshot
  @Column(length = 512)
  String title;

  @Column(length = 32)
  String postType;

  @Column(name = "is_public", nullable = false)
  boolean isPublic;
}