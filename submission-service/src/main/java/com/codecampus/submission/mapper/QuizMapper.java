package com.codecampus.submission.mapper;

import com.codecampus.quiz.grpc.QuizExerciseDto;
import com.codecampus.submission.entity.Exercise;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QuizMapper {
  default QuizExerciseDto toQuizExerciseDtoFromExercise(
      Exercise exercise) {
    return QuizExerciseDto.newBuilder()
        .setId(exercise.getId())
        .setTitle(exercise.getTitle())
        .setDescription(Optional.ofNullable(exercise.getDescription())
            .orElse(""))
        .setTotalPoints(
            exercise.getQuizDetail() == null ? 0 :
                exercise.getQuizDetail().getTotalPoints())
        .setNumQuestions(
            exercise.getQuizDetail() == null ? 0 :
                exercise.getQuizDetail().getNumQuestions())
        .setDuration(exercise.getDuration())
        .setPublicAccessible(exercise.isVisibility())
        .setCreatedBy(
            Optional.ofNullable(exercise.getCreatedBy())
                .orElse(""))
        .build();
  }
}
