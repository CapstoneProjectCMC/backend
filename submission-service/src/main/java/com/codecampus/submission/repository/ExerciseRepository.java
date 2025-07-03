package com.codecampus.submission.repository;

import com.codecampus.submission.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository
    extends JpaRepository<Exercise, String>
{
}

