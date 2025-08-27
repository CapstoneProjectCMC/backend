package com.codecampus.profile.repository;

import com.codecampus.profile.entity.Exercise;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository
    extends Neo4jRepository<Exercise, String> {
  @Query("MATCH (e:Exercise {exerciseId:$exerciseId}) RETURN e LIMIT 1")
  Optional<Exercise> findByExerciseId(@Param("exerciseId") String exerciseId);

  @Query("""
      MERGE (e:Exercise {exerciseId: $exerciseId})
      RETURN e
      """)
  Exercise mergeByExerciseId(@Param("exerciseId") String exerciseId);

  Optional<Exercise> findFirstByExerciseId(String exerciseId);

  // Upsert đầy đủ field khi có payload từ ExerciseEvent
  @Query("""
        MERGE (e:Exercise {exerciseId: $exerciseId})
        ON CREATE SET
          e.title = $title,
          e.type = $type,
          e.difficulty = coalesce($difficulty, 0)
        ON MATCH SET
          e.title = coalesce($title, e.title),
          e.type = coalesce($type, e.type),
          e.difficulty = coalesce($difficulty, e.difficulty)
        RETURN e
      """)
  Exercise upsertExercise(
      String exerciseId,
      String title,
      String type,
      Integer difficulty);
}
