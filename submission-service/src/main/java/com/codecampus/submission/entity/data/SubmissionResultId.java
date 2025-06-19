package com.codecampus.submission.entity.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Embeddable
public class SubmissionResultId implements Serializable
{
  @Column(name = "submission_id")
  String submissionId;

  @Column(name = "test_case_id")
  String testCaseId;
}
