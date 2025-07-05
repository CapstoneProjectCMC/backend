package com.codecampus.submission.service;

import com.codecampus.submission.dto.request.CreateExerciseRequest;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.exception.AppException;
import com.codecampus.submission.exception.ErrorCode;
import com.codecampus.submission.helper.AuthenticationHelper;
import com.codecampus.submission.mapper.ExerciseMapper;
import com.codecampus.submission.repository.ExerciseRepository;
import com.codecampus.submission.service.client.GrpcQuizClient;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseService
{
  ExerciseRepository exerciseRepository;
  GrpcQuizClient grpcQuizClient;

  ExerciseMapper exerciseMapper;

  @Transactional
  public Exercise createExercise(
      CreateExerciseRequest request)
  {
    Exercise exercise = exerciseRepository
        .save(exerciseMapper.toExercise(
            request, AuthenticationHelper.getMyUserId()));
    grpcQuizClient.pushExercise(exercise);
    return exercise;
  }

  public Exercise getExerciseOrThrow(
      String exerciseId)
  {
    return exerciseRepository.findById(exerciseId)
        .orElseThrow(
            () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
        );
  }
}
