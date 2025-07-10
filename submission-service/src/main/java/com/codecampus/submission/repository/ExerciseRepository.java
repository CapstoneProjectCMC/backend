package com.codecampus.submission.repository;

import com.codecampus.submission.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository
        extends JpaRepository<Exercise, String> {
    List<Exercise> findByUserId(String teacherId);
}

