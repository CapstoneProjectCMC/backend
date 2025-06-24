package com.codecampus.quiz.repository;

import com.codecampus.quiz.entity.QuizOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizOptionRepository
    extends JpaRepository<QuizOption, String>
{
}
