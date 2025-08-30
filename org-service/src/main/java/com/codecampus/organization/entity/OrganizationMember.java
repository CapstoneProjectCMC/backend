package com.codecampus.organization.entity;

import com.codecampus.constant.ScopeType;
import com.codecampus.organization.entity.audit.AuditMetadata;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "organization_members")
@SQLDelete(sql = "UPDATE organization_members SET deleted_by = 'system', deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class OrganizationMember extends AuditMetadata {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @Column(name = "user_id", nullable = false)
  String userId; // GUID từ identity

  @Enumerated(EnumType.STRING)
  @Column(name = "scope_type", nullable = false, length = 32)
  ScopeType scopeType; // Organization | Block

  @Column(name = "scope_id", nullable = false)
  String scopeId; // id của Organization hoặc Block

  @Column(nullable = false, length = 32)
  String role; // ADMIN|TEACHER|STUDENT

  @Column(nullable = false)
  boolean isActive = true;

  @Column(nullable = false)
  boolean isPrimary = false; // primary org cho scopeType=Organization
}