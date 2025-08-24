package com.codecampus.submission.service.grpc;

import com.codecampus.submission.grpc.CreateCodeSubmissionRequest;
import com.codecampus.submission.grpc.CreateQuizSubmissionRequest;
import com.codecampus.submission.grpc.SubmissionSyncServiceGrpc;
import com.codecampus.submission.service.ExerciseService;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

@GrpcService
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubmissionSyncServiceImpl
    extends SubmissionSyncServiceGrpc.SubmissionSyncServiceImplBase {

  ExerciseService exerciseService;

  @Override
  @Transactional
  public void createQuizSubmission(
      CreateQuizSubmissionRequest request,
      StreamObserver<Empty> responseObserver) {

    exerciseService.createQuizSubmission(request);

    responseObserver.onNext(Empty.getDefaultInstance());
    responseObserver.onCompleted();
  }

  @Override
  public void createCodeSubmission(
      CreateCodeSubmissionRequest request,
      StreamObserver<Empty> responseObserver) {

    exerciseService.createCodeSubmission(request);

    responseObserver.onNext(Empty.getDefaultInstance());
    responseObserver.onCompleted();
  }
}
