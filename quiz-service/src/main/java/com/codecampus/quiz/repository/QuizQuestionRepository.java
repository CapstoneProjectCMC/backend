package com.codecampus.quiz.repository;

import com.codecampus.quiz.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizQuestionRepository
    extends JpaRepository<QuizQuestion, String>
{
}
