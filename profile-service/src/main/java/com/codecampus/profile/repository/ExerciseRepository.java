package com.codecampus.profile.repository;

import com.codecampus.profile.entity.Exercise;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExerciseRepository
        extends Neo4jRepository<Exercise, String> {
    Optional<Exercise> findByExerciseId(String title);

}
