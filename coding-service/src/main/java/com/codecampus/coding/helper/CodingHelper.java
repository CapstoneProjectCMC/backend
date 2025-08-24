package com.codecampus.coding.helper;

import com.codecampus.coding.entity.CodingExercise;
import com.codecampus.coding.exception.AppException;
import com.codecampus.coding.exception.ErrorCode;
import com.codecampus.coding.grpc.LoadCodingResponse;
import com.codecampus.coding.repository.AssignmentRepository;
import com.codecampus.coding.repository.CodingExerciseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CodingHelper {

  CodingExerciseRepository codingExerciseRepository;
  AssignmentRepository assignmentRepository;

  public CodingExercise findCodingOrThrow(String exerciseId) {
    return codingExerciseRepository
        .findById(exerciseId)
        .orElseThrow(
            () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
        );
  }

  public boolean hasAccessOnLoadCodingResponse(
      LoadCodingResponse loadCodingResponse,
      String userId,
      String username,
      boolean teacher) {

    boolean publicAccessible = loadCodingResponse
        .getExercise()
        .getPublicAccessible();
    boolean owner =
        username != null && username.equalsIgnoreCase(
            loadCodingResponse.getExercise()
                .getCreatedBy()
        );

    boolean assigned =
        userId != null && assignmentRepository
            .existsByExerciseIdAndStudentId(
                loadCodingResponse.getExercise()
                    .getId(), userId);

    return publicAccessible || owner || teacher || assigned;
  }

  public boolean hasAccessOnCodingExercise(
      CodingExercise coding,
      String userId,
      String username,
      boolean teacher) {

    boolean publicAccessible =
        coding.isPublicAccessible();

    boolean owner =
        username != null &&
            username.equalsIgnoreCase(coding.getCreatedBy());

    boolean assigned = userId != null &&
        assignmentRepository.existsByExerciseIdAndStudentId(
            coding.getId(), userId);

    return publicAccessible || owner || teacher || assigned;
  }
}
