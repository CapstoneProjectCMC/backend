package com.codecampus.profile.repository;

import com.codecampus.profile.entity.Classroom;
import com.codecampus.profile.entity.Org;
import com.codecampus.profile.entity.properties.exercise.AssignedClassExercise;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassroomRepository
    extends Neo4jRepository<Classroom, String>
{
  @Query(value = """
    MATCH (c:Class {classId:$classId})-[a:ASSIGNED_CLASS_EXERCISE]->(e:Exercise)
    RETURN a, e ORDER BY e.title
    """,
      countQuery = """
    MATCH (:Class {classId:$classId})-[a:ASSIGNED_CLASS_EXERCISE]->(:Exercise)
    RETURN count(a)
    """)
  Page<AssignedClassExercise> findAssignedExercises(
      String classId, Pageable pageable);

  @Query("""
    MATCH (c:Class {classId:$classId})-[:BELONGS_TO]->(o:Organization)
    RETURN o LIMIT 1
  """)
  Optional<Org> findOrgOfClass(String classId);
}
