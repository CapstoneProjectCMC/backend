package com.codecampus.coding.service.grpc;

import com.codecampus.coding.grpc.CodingPlayServiceGrpc;
import com.codecampus.coding.grpc.SubmitCodeRequest;
import com.codecampus.coding.grpc.SubmitCodeResponse;
import com.codecampus.coding.service.CodeJudgeService;
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
}
