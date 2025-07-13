package com.codecampus.submission.service.grpc;

import com.codecampus.coding.grpc.AddCodingDetailRequest;
import com.codecampus.coding.grpc.AddTestCaseRequest;
import com.codecampus.coding.grpc.CodingSyncServiceGrpc;
import com.codecampus.coding.grpc.CreateCodingExerciseRequest;
import com.codecampus.coding.grpc.UpsertTestCaseRequest;
import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.entity.CodingDetail;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.TestCase;
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
    public void pushCodingDetail(CodingDetail codingDetail) {
        AddCodingDetailRequest addCodingRequest =
                AddCodingDetailRequest.newBuilder()
                        .setDetail(
                                codingMapper.toCodingDetailDtoFromCodingDetail(
                                        codingDetail))
                        .build();

        stub.addCodingDetail(addCodingRequest);
    }

    @Transactional
    public void pushTestCase(TestCase testCase) {
        AddTestCaseRequest addTestRequest = AddTestCaseRequest.newBuilder()
                .setTestCase(codingMapper.toTestCaseDtoFromTestCase(testCase))
                .build();

        stub.addTestCase(addTestRequest);
    }

    @Transactional
    public void upsertTestCase(TestCase tc) {
        UpsertTestCaseRequest req = UpsertTestCaseRequest.newBuilder()
                .setTestCase(codingMapper.toTestCaseDtoFromTestCase(tc))
                .build();
        stub.upsertTestCase(req);
    }
}
