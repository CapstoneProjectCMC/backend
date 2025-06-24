package com.codecampus.submission.mapper;

import com.codecampus.submission.dto.request.ExerciseCreationRequest;
import com.codecampus.submission.dto.response.exercise.ExerciseSummaryResponse;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.mapper.filter.RoleFilterMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExerciseMapper
    extends RoleFilterMapper<Exercise, ExerciseSummaryResponse>
{

  ExerciseSummaryResponse toExerciseSummaryResponse(Exercise exercise);

  @BeanMapping(ignoreUnmappedSourceProperties = {"exerciseType", "coding", "quiz"})
  Exercise toExercise(ExerciseCreationRequest request);

  @Override
  default void filterForUser(ExerciseSummaryResponse response)
  {
    RoleFilterMapper.super.filterForUser(response);
    response.setUserId(null);
  }

  @Override
  default void filterForTeacher(
      ExerciseSummaryResponse response)
  {
    RoleFilterMapper.super.filterForTeacher(response);
  }
}
