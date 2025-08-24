package com.codecampus.submission.service.grpc;

import com.codecampus.coding.grpc.AddCodingDetailRequest;
import com.codecampus.coding.grpc.AddTestCaseRequest;
import com.codecampus.coding.grpc.CodingSyncServiceGrpc;
import com.codecampus.coding.grpc.CreateCodingExerciseRequest;
import com.codecampus.coding.grpc.SoftDeleteAssignmentRequest;
import com.codecampus.coding.grpc.SoftDeleteRequest;
import com.codecampus.coding.grpc.SoftDeleteTestCaseRequest;
import com.codecampus.coding.grpc.UpsertAssignmentRequest;
import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.entity.Assignment;
import com.codecampus.submission.entity.CodingDetail;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.TestCase;
import com.codecampus.submission.mapper.AssignmentMapper;
import com.codecampus.submission.mapper.CodingMapper;
import io.grpc.StatusRuntimeException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GrpcCodingClient {

  CodingSyncServiceGrpc.CodingSyncServiceBlockingStub stub;
  CodingMapper codingMapper;
  AssignmentMapper assignmentMapper;

  @Transactional
  public void pushExercise(Exercise exercise) {
    if (exercise.getExerciseType() != ExerciseType.CODING) {
      return;
    }

    try {
      CreateCodingExerciseRequest createRequest =
          CreateCodingExerciseRequest.newBuilder()
              .setExercise(
                  codingMapper.toCodingExerciseDtoFromExercise(
                      exercise))
              .build();
      stub.createCodingExercise(createRequest);
    } catch (StatusRuntimeException ex) {
      log.error("[gRPC] pushExercise lỗi: {}", ex.getStatus(), ex);
      throw ex;
    }
  }

  @Transactional
  public void softDeleteExercise(String exerciseId) {
    stub.softDeleteExercise(SoftDeleteRequest
        .newBuilder()
        .setId(exerciseId)
        .build());
  }

  @Transactional
  public void pushCodingDetail(
      String exerciseId,
      CodingDetail codingDetail) {
    AddCodingDetailRequest addCodingRequest =
        AddCodingDetailRequest.newBuilder()
            .setExerciseId(exerciseId)
            .setCodingDetail(
                codingMapper.toCodingDetailDtoFromCodingDetail(
                    codingDetail))
            .build();
    stub.addCodingDetail(addCodingRequest);
  }

  @Transactional
  public void pushTestCase(
      String exerciseId,
      TestCase testCase) {
    AddTestCaseRequest addTestRequest =
        AddTestCaseRequest.newBuilder()
            .setExerciseId(exerciseId)
            .setTestCase(codingMapper.toTestCaseDtoFromTestCase(
                testCase))
            .build();
    stub.addTestCase(addTestRequest);
  }

  @Transactional
  public void softDeleteTestCase(String exerciseId, String testCaseId) {
    stub.softDeleteTestCase(SoftDeleteTestCaseRequest
        .newBuilder()
        .setExerciseId(exerciseId)
        .setTestCaseId(testCaseId)
        .build());
  }

  @Transactional
  public void pushAssignment(Assignment assignment) {
    UpsertAssignmentRequest request = UpsertAssignmentRequest.newBuilder()
        .setAssignment(
            assignmentMapper.toCodingAssignmentDtoFromAssignment(
                assignment))
        .build();

    stub.upsertAssignment(request);
  }

  @Transactional
  public void softDeleteAssignment(String assignmentId) {
    stub.softDeleteAssignment(
        SoftDeleteAssignmentRequest.newBuilder()
            .setId(assignmentId)
            .build());
  }
}
