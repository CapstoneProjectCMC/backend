package com.codecampus.quiz.repository;

import com.codecampus.quiz.entity.QuizExercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizExerciseRepository
    extends JpaRepository<QuizExercise, String>
{
}
