package com.codecampus.submission.mapper;

import com.codecampus.submission.dto.request.CreateExerciseRequest;
import com.codecampus.submission.entity.Exercise;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ExerciseMapper
{
  @Mapping(target = "userId", expression = "java(userId)")
  Exercise toExercise(
      CreateExerciseRequest request,
      @Context String userId);
}
