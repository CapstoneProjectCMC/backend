package com.codecampus.organization.mapper;

import com.codecampus.organization.entity.OrganizationExercise;
import dtos.ExerciseSummary;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

  default ExerciseSummary toExerciseSummary(
      OrganizationExercise e) {
    if (e == null) {
      return null;
    }
    return new ExerciseSummary(
        e.getExerciseId(),
        e.getTitle(),
        e.getExerciseType(),
        e.getDifficulty(),
        e.isVisibility(),
        e.getOrgId(),
        /* created   */ false,
        // thông tin này thuộc cá nhân HS/teacher; để false
        /* completed */ false
    );
  }
}
