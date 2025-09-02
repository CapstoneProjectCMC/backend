package com.codecampus.coding.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
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
@SQLDelete(sql = "UPDATE code_submission_result " +
    "SET deleted_by = ? , deleted_at = now() " +
    "WHERE submission_id = ? AND test_case_id = ?")
@Where(clause = "deleted_at IS NULL")
public class CodeSubmissionResult {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "submission_id", nullable = false)
  CodeSubmission submission;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_case_id", nullable = false)
  TestCase testCase;

  boolean passed;
  Integer runtimeMs;
  Integer memoryMb;
  @Lob
  String output;
  @Lob
  String errorMessage;
}
