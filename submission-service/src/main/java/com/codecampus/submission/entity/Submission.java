package com.codecampus.submission.entity;

import com.codecampus.submission.constant.submission.SubmissionStatus;
import com.codecampus.submission.entity.audit.AuditMetadata;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "submission")
@SQLDelete(sql = "UPDATE submission SET deleted_at = now(), deleted_by = ? WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Submission extends AuditMetadata {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "exercise_id", nullable = false)
  Exercise exercise;

  @Column(name = "user_id", nullable = false)
  String userId;

  @Column(name = "submitted_at", nullable = false, updatable = false)
  Instant submittedAt;

  @Column(columnDefinition = "smallint")
  Integer score;

  @Enumerated(EnumType.ORDINAL)
  @Column(nullable = false, columnDefinition = "smallint")
  SubmissionStatus status;

  // ----- code-only fields -----
  @Column(length = 20)
  String language;

  @Column(name = "source_code", columnDefinition = "text")
  String sourceCode;

  int runtime;
  int memoryUsed;
}
