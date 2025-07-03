package com.codecampus.submission.repository;

import com.codecampus.submission.entity.Submission;
import com.codecampus.submission.entity.SubmissionResultDetail;
import com.codecampus.submission.entity.data.SubmissionResultId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionResultRepository
    extends JpaRepository<SubmissionResultDetail, SubmissionResultId>
{
  List<SubmissionResultDetail> findBySubmission(
      Submission submission);
}