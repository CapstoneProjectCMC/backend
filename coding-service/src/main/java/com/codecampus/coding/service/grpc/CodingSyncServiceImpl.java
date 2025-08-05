package com.codecampus.coding.service.grpc;

import com.codecampus.coding.grpc.AddCodingDetailRequest;
import com.codecampus.coding.grpc.AddTestCaseRequest;
import com.codecampus.coding.grpc.CodingSyncServiceGrpc;
import com.codecampus.coding.grpc.CreateCodingExerciseRequest;
import com.codecampus.coding.grpc.SoftDeleteRequest;
import com.codecampus.coding.grpc.SoftDeleteTestCaseRequest;
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
            CreateCodingExerciseRequest createCodingExerciseRequest,
            StreamObserver<Empty> responseObserver) {

        codingService.createCodingExercise(createCodingExerciseRequest);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void addCodingDetail(
            AddCodingDetailRequest addCodingDetailRequest,
            StreamObserver<Empty> responseObserver) {

        codingService.addCodingDetail(addCodingDetailRequest);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void addTestCase(
            AddTestCaseRequest addTestCaseRequest,
            StreamObserver<Empty> responseObserver) {

        codingService.addTestCase(addTestCaseRequest);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void softDeleteExercise(
            SoftDeleteRequest softDeleteRequest,
            StreamObserver<Empty> responseObserver) {

        codingService.softDeleteExercise(softDeleteRequest.getId());

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void softDeleteTestCase(
            SoftDeleteTestCaseRequest softDeleteTestCaseRequest,
            StreamObserver<Empty> responseObserver) {

        codingService.softDeleteTestCase(
                softDeleteTestCaseRequest.getTestCaseId(),
                softDeleteTestCaseRequest.getTestCaseId()
        );

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
