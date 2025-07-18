package com.codecampus.submission.repository;

import com.codecampus.submission.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository
        extends JpaRepository<Submission, String> {
    List<Submission> findByUserId(
            String userId);

    Optional<Submission> findByExerciseIdAndUserId(
            String exerciseId,
            String userId);
}

