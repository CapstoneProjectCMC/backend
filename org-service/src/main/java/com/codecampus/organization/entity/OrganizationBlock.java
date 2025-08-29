package com.codecampus.organization.entity;

import com.codecampus.organization.entity.audit.AuditMetadata;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Entity
@Table(name = "organization_blocks",
    uniqueConstraints = @UniqueConstraint(columnNames = {"org_id", "code"}))
@SQLDelete(sql = "UPDATE organization_blocks SET deleted_by = 'system', deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class OrganizationBlock extends AuditMetadata {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @Column(name = "org_id", nullable = false)
  String orgId;

  @Column(nullable = false, length = 150)
  String name;

  @Column(nullable = false, length = 50)
  String code; // ví dụ: G10, SCI, HR...

  String description;
}