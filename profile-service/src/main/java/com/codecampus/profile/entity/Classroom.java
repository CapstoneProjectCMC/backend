package com.codecampus.profile.entity;

import com.codecampus.profile.entity.properties.exercise.AssignedClassExercise;
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
@Node("Class")
public class Classroom
{
  @Id
  @GeneratedValue(generatorClass = UUIDStringGenerator.class)
  String id;

  String classId;
  String name;

  @Relationship(
      type = "BELONGS_TO",
      direction = Relationship.Direction.OUTGOING
  )
  Org organization;

  @Relationship(type = "ASSIGNED_CLASS_EXERCISE")
  Set<AssignedClassExercise> exercises = new HashSet<>();
}
