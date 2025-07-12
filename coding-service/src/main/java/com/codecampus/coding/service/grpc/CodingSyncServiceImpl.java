package com.codecampus.coding.service.grpc;

import com.codecampus.coding.grpc.AddCodingDetailRequest;
import com.codecampus.coding.grpc.AddTestCaseRequest;
import com.codecampus.coding.grpc.CodingSyncServiceGrpc;
import com.codecampus.coding.grpc.CreateCodingExerciseRequest;
import com.codecampus.coding.service.CodingService;
import com.google.protobuf.Empty;
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
public class CodingSyncServiceImpl
        extends CodingSyncServiceGrpc.CodingSyncServiceImplBase {

    CodingService codingService;

    @Override
    public void createCodingExercise(
            CreateCodingExerciseRequest request,
            StreamObserver<Empty> responseObserver) {

        codingService.createCodingExercise(request);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void addCodingDetail(AddCodingDetailRequest request,
                                StreamObserver<Empty> responseObserver) {

        codingService.addCodingDetail(request);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void addTestCase(AddTestCaseRequest request,
                            StreamObserver<Empty> responseObserver) {

        codingService.addTestCase(request);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
