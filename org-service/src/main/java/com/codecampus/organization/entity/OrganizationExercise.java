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
    name = "organization_exercises",
    indexes = {
        @Index(name = "idx_org_exercises_org", columnList = "org_id"),
        @Index(name = "idx_org_exercises_exercise", columnList = "exercise_id", unique = false)
    }
)
@SQLDelete(sql = "UPDATE organization_exercises " +
    "SET deleted_by = 'system', deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class OrganizationExercise extends AuditMetadata {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @Column(name = "org_id", nullable = false, length = 36)
  String orgId;

  @Column(name = "exercise_id", nullable = false, length = 36)
  String exerciseId;

  // --- snapshot fields để phục vụ listing nhanh, tránh gọi chéo service ---
  @Column(name = "title", length = 255)
  String title;

  @Column(name = "exercise_type", length = 16) // QUIZ | CODING
  String exerciseType;

  @Column(name = "difficulty", length = 16) // giữ nguyên string từ payload
  String difficulty;

  @Column(name = "visibility", nullable = false)
  boolean visibility;

  @Column(name = "free_for_org", nullable = false)
  boolean freeForOrg;
}