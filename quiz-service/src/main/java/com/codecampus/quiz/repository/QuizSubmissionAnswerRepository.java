package com.codecampus.quiz.repository;

import com.codecampus.quiz.entity.QuizSubmissionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizSubmissionAnswerRepository
        extends JpaRepository<QuizSubmissionAnswer, String> {
}
