package com.codecampus.profile.entity;

import com.codecampus.profile.entity.properties.exercise.AssignedOrgExercise;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Node("Organization")
public class Org
{
  @Id
  @GeneratedValue(generatorClass = UUIDStringGenerator.class)
  String id;

  String orgId;
  String orgName;
  String logoUrl;
  String description;

  @Relationship(type = "HAS_CLASS")
  Set<Classroom> classrooms = new HashSet<>();

  @Relationship(type = "ASSIGNED_ORG_EXERCISE")
  Set<AssignedOrgExercise> exercises = new HashSet<>();
}
