package com.codecampus.coding.repository;

import com.codecampus.coding.entity.CodingExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodingExerciseRepository
        extends JpaRepository<CodingExercise, String> {
}

