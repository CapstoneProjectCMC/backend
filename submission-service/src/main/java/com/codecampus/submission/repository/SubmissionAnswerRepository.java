package com.codecampus.submission.repository;

import com.codecampus.submission.entity.SubmissionAnswer;
import com.codecampus.submission.entity.data.SubmissionAnswerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionAnswerRepository
    extends JpaRepository<SubmissionAnswer, SubmissionAnswerId> {
}

