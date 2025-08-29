package com.codecampus.organization.entity;

import com.codecampus.organization.entity.audit.AuditMetadata;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "organizations")
@SQLDelete(sql = "UPDATE organizations SET deleted_by = 'system', deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Organization extends AuditMetadata {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @Column(nullable = false, unique = true, length = 150)
  String name;

  String description;
  String logoUrl;
  String email;
  String phone;
  String address;
  String status;     // Active/Inactive/Archived...
}