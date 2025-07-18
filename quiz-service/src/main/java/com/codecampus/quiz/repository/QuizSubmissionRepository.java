package com.codecampus.quiz.repository;

import com.codecampus.quiz.entity.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizSubmissionRepository
    extends JpaRepository<QuizSubmission, String>
{
}
