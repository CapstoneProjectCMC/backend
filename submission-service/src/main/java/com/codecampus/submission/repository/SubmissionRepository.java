package com.codecampus.submission.repository;

import com.codecampus.submission.entity.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository
    extends JpaRepository<Submission, String> {
  Page<Submission> findByUserId(
      String userId,
      Pageable pageable
  );
}
