package com.codecampus.quiz.repository;

import com.codecampus.quiz.entity.QuizExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizExerciseRepository
        extends JpaRepository<QuizExercise, String> {
}
