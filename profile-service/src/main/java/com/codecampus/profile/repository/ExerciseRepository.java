package com.codecampus.profile.repository;

import com.codecampus.profile.entity.Exercise;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository
    extends Neo4jRepository<Exercise, String>
{

}
