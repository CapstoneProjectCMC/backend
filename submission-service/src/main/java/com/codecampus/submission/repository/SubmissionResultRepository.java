package com.codecampus.submission.repository;

import com.codecampus.submission.entity.Submission;
import com.codecampus.submission.entity.SubmissionResultDetail;
import com.codecampus.submission.entity.data.SubmissionResultId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionResultRepository
        extends JpaRepository<SubmissionResultDetail, SubmissionResultId> {
    List<SubmissionResultDetail> findBySubmission(
            Submission submission);
}