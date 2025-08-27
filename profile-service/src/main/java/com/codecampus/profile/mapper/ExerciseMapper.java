package com.codecampus.profile.mapper;

import com.codecampus.profile.entity.Exercise;
import dtos.ExerciseSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring"
)
public interface ExerciseMapper {

  @Mapping(target = "exerciseId", source = "id")
  @Mapping(target = "type", source = "exerciseType")
  @Mapping(target = "difficulty",
      expression = "java(parseDifficulty(exerciseSummary.difficulty()))")
  Exercise toExerciseFromExerciseSummary(
      ExerciseSummary exerciseSummary);

  default int parseDifficulty(String raw) {
    if (raw == null) {
      return 0;
    }
    String s = raw.trim();
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException ignore) {
      switch (s.toUpperCase()) {
        case "EASY":
          return 0;
        case "MEDIUM":
          return 1;
        default:
          return 2;
      }
    }
  }
}
