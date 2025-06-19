package com.codecampus.submission.entity;

import com.codecampus.submission.entity.data.SubmissionResultId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "submission_result_detail")
public class SubmissionResultDetail
{
  @EmbeddedId
  SubmissionResultId id;

  @MapsId("submissionId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "submission_id")
  Submission submission;

  @MapsId("testCaseId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_case_id")
  TestCase testCase;

  @Column(nullable = false)
  boolean passed;

  @Column(name = "run_time_ts")
  Integer runTimeTs;

  Integer memoryUsed;

  @Column(columnDefinition = "text")
  String output;

  @Column(name = "error_message", columnDefinition = "text")
  String errorMessage;
}
