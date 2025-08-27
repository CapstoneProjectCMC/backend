package com.codecampus.submission.mapper;

import com.codecampus.submission.dto.data.ExerciseSummaryDto;
import com.codecampus.submission.dto.request.CreateExerciseRequest;
import com.codecampus.submission.dto.request.UpdateExerciseRequest;
import com.codecampus.submission.dto.response.quiz.ExerciseQuizResponse;
import com.codecampus.submission.entity.Exercise;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ExerciseMapper {

  @Mapping(target = "userId", expression = "java(userId)")
  @Mapping(target = "visibility",
      expression =
          "java(request.visibility() != null ?"
              +
              "request.visibility(): "
              +
              "(request.orgId()==null || request.orgId().isBlank()))")
  Exercise toExerciseFromCreateExerciseRequest(
      CreateExerciseRequest request,
      @Context String userId);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchUpdateExerciseRequestToExercise(
      UpdateExerciseRequest request,
      @MappingTarget Exercise exercise
  );

  ExerciseQuizResponse toExerciseQuizResponseFromExercise(
      Exercise e);

  default ExerciseSummaryDto toExerciseSyncDtoFromExercise(
      Exercise exercise,
      boolean created,
      boolean completed) {
    return new ExerciseSummaryDto(
        exercise.getId(),
        exercise.getTitle(),
        exercise.getExerciseType(),
        exercise.getDifficulty(),
        exercise.isVisibility(),
        exercise.getOrgId(),
        created,
        completed
    );
  }
}
