package com.codecampus.coding.service.grpc;

import com.codecampus.coding.grpc.CodingPlayServiceGrpc;
import com.codecampus.coding.grpc.LoadCodingRequest;
import com.codecampus.coding.grpc.LoadCodingResponse;
import com.codecampus.coding.grpc.SubmitCodeRequest;
import com.codecampus.coding.grpc.SubmitCodeResponse;
import com.codecampus.coding.service.CodeJudgeService;
import com.codecampus.coding.service.CodingService;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CodingPlayServiceImpl
    extends CodingPlayServiceGrpc.CodingPlayServiceImplBase {

  CodingService codingService;
  CodeJudgeService codeJudgeService;

  @Override
  public void submitCode(
      SubmitCodeRequest request,
      StreamObserver<SubmitCodeResponse> responseObserver) {

    SubmitCodeResponse submitCodeResponse =
        codeJudgeService.judgeCodeSubmission(request);

    responseObserver.onNext(submitCodeResponse);
    responseObserver.onCompleted();
  }

  @Override
  public void loadCoding(
      LoadCodingRequest request,
      StreamObserver<LoadCodingResponse> responseObserver) {

    LoadCodingResponse response =
        codingService.loadCoding(
            request.getExerciseId());

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
