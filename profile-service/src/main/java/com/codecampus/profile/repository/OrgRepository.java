package com.codecampus.profile.repository;

import com.codecampus.profile.entity.Org;
import com.codecampus.profile.entity.properties.exercise.AssignedOrgExercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgRepository
    extends Neo4jRepository<Org, String>
{

  @Query(value = """
      MATCH (o:Organization {orgId:$orgId})-[a:ASSIGNED_ORG_EXERCISE]->(e:Exercise)
      RETURN a, e ORDER BY e.title
      """,
      countQuery = """
          MATCH (o:Organization {orgId:$orgId})-[a:ASSIGNED_ORG_EXERCISE]->(:Exercise)
          RETURN count(a)
          """)
  Page<AssignedOrgExercise> findAssignedExercises(
      String orgId, Pageable pageable);
}
