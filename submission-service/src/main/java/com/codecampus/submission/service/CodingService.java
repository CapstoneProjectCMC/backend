package com.codecampus.submission.service;

import com.codecampus.submission.constant.sort.SortField;
import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.dto.common.PageResponse;
import com.codecampus.submission.dto.request.coding.AddCodingDetailRequest;
import com.codecampus.submission.dto.request.coding.TestCaseDto;
import com.codecampus.submission.dto.request.coding.TestCasePatchDto;
import com.codecampus.submission.dto.request.coding.UpdateCodingDetailRequest;
import com.codecampus.submission.dto.request.coding.UpdateCodingDetailWithTestCaseRequest;
import com.codecampus.submission.dto.request.coding.UpdateTestCaseRequest;
import com.codecampus.submission.entity.CodingDetail;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.TestCase;
import com.codecampus.submission.exception.AppException;
import com.codecampus.submission.exception.ErrorCode;
import com.codecampus.submission.helper.AuthenticationHelper;
import com.codecampus.submission.helper.CodingHelper;
import com.codecampus.submission.helper.PageResponseHelper;
import com.codecampus.submission.helper.SortHelper;
import com.codecampus.submission.mapper.CodingMapper;
import com.codecampus.submission.mapper.TestCaseMapper;
import com.codecampus.submission.repository.CodingDetailRepository;
import com.codecampus.submission.repository.ExerciseRepository;
import com.codecampus.submission.repository.TestCaseRepository;
import com.codecampus.submission.service.grpc.GrpcCodingClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CodingService {
    CodingDetailRepository codingDetailRepository;
    TestCaseRepository testCaseRepository;
    ExerciseRepository exerciseRepository;
    GrpcCodingClient grpcCodingClient;

    TestCaseMapper testCaseMapper;
    CodingMapper codingMapper;
    private final CodingHelper codingHelper;

    @Transactional
    public CodingDetail addCodingDetail(
            String exerciseId,
            AddCodingDetailRequest addCodingRequest,
            boolean returnCodingDetail) {
        Exercise exercise = getExerciseOrThrow(exerciseId);

        Assert.isTrue(
                exercise.getExerciseType() == ExerciseType.CODING,
                "Exercise không phải CODE"
        );

        if (exercise.getCodingDetail() != null) {
            throw new AppException(ErrorCode.EXERCISE_TYPE);
        }

        CodingDetail codingDetail =
                codingMapper.toCodingDetailFromAddCodingRequest(
                        addCodingRequest);
        /*
         * CodingDetail có @Id trùng với id của Exercise (@MapsId).
         * Trong phương thức CodingService.addCodingDetail() ta tự đặt luôn giá trị id, nên khi
         * JpaRepository.save() được gọi, Hibernate coi entity
         * là “đã tồn tại” (vì id ≠ null) và phát lệnh UPDATE thay vì INSERT.
         * Vì hàng chưa hề có trong bảng coding_detail, số bản ghi ảnh hưởng = 0 ⇒
         * ObjectOptimisticLockingFailureException.
         */
        // codingDetail.setId(exerciseId);
        codingDetail.setExercise(exercise);
        addCodingRequest.testCases().forEach(tcDto -> {
            TestCase testCase = testCaseMapper.toTestCaseFromTestCaseDto(tcDto);
            testCase.setCodingDetail(codingDetail);
            codingDetail.getTestCases().add(testCase);
        });

        codingDetailRepository.save(codingDetail);

        grpcCodingClient.pushCodingDetail(exerciseId, codingDetail);

        if (returnCodingDetail) {
            return codingDetail;
        }

        return null;
    }

    @Transactional
    public TestCase addTestCase(
            String exerciseId,
            TestCaseDto testCaseDto,
            boolean returnTestCase) throws BadRequestException {

        Exercise exercise = getExerciseOrThrow(exerciseId);
        CodingDetail codingDetail = Optional
                .ofNullable(exercise.getCodingDetail())
                .orElseThrow(
                        () -> new BadRequestException("Chưa có CodingDetail")
                );

        TestCase testCase =
                testCaseMapper.toTestCaseFromTestCaseDto(testCaseDto);
        testCase.setCodingDetail(codingDetail);
        codingDetail.getTestCases().add(testCase);
        testCaseRepository.save(testCase);

        grpcCodingClient.pushTestCase(exerciseId, testCase);

        if (returnTestCase) {
            return testCase;
        }
        return null;
    }

    @Transactional
    public void updateTestCase(
            String exerciseId,
            String testCaseId,
            UpdateTestCaseRequest request) {
        Exercise exercise = getExerciseOrThrow(exerciseId);

        TestCase testCase = exercise.getCodingDetail()
                .getTestCases()
                .stream()
                .filter(c -> c.getId().equals(testCaseId))
                .findFirst()
                .orElseThrow(
                        () -> new AppException(ErrorCode.TESTCASE_NOT_FOUND)
                );

        testCaseMapper.patchUpdateTestCaseRequestToTestCase(testCase, request);

        grpcCodingClient.pushTestCase(exerciseId, testCase);
    }


    @Transactional
    public void updateCodingDetailWithTestCaseRequest(
            String exerciseId,
            UpdateCodingDetailWithTestCaseRequest request) {

        Exercise exercise = getExerciseOrThrow(exerciseId);
        CodingDetail codingDetail =
                Optional.ofNullable(exercise.getCodingDetail())
                        .orElseThrow(() -> new AppException(
                                ErrorCode.CODING_DETAIL_NOT_FOUND));

        codingMapper.patchUpdateCodingDetailRequestToCodingDetail(
                codingDetail,
                new UpdateCodingDetailRequest(
                        request.topic(), request.allowedLanguages(),
                        request.input(),
                        request.output(), request.constraintText(),
                        request.timeLimit(),
                        request.memoryLimit(), request.maxSubmissions(),
                        request.codeTemplate(), request.solution()));

        if (request.testCases() != null && !request.testCases().isEmpty()) {

            Map<String, TestCase> current = codingDetail
                    .getTestCases()
                    .stream()
                    .collect(Collectors.toMap(TestCase::getId,
                            testCase -> testCase));


            for (TestCasePatchDto testCasePatchDto : request.testCases()) {

                TestCase testCase = Optional
                        .ofNullable(current.get(testCasePatchDto.id()))
                        .orElseThrow(
                                () -> new AppException(
                                        ErrorCode.TESTCASE_NOT_FOUND)
                        );

                // --- Xoá mềm ---
                if (Boolean.TRUE.equals(testCasePatchDto.delete())) {
                    if (!testCase.isDeleted()) {
                        testCase.markDeleted(
                                AuthenticationHelper.getMyUsername());
                        grpcCodingClient.softDeleteTestCase(
                                exerciseId, testCase.getId());
                    }
                    continue;
                }

                // --- Cập nhật ---
                TestCase existing = current.get(testCasePatchDto.id());
                codingHelper.patchTestCasePatchDtoToTestCase(
                        existing, testCasePatchDto);
                grpcCodingClient.pushTestCase(exerciseId, existing);
            }
        }

        codingDetailRepository.save(codingDetail);
        grpcCodingClient.pushCodingDetail(exerciseId, codingDetail);
    }

    @Transactional
    public void softDeleteTestCase(
            String exerciseId,
            String testCaseId) {
        getExerciseOrThrow(exerciseId);
        TestCase testCase = getTestCaseOrThrow(testCaseId);
        testCase.markDeleted(AuthenticationHelper.getMyUsername());

        grpcCodingClient.softDeleteTestCase(exerciseId, testCaseId);
    }

    @Transactional(readOnly = true)
    public PageResponse<TestCase> getTestCases(
            String exerciseId,
            int page, int size,
            SortField sortBy, boolean asc) {

        Pageable pageable = PageRequest.of(page - 1, size,
                SortHelper.build(sortBy, asc));

        Page<TestCase> pageData =
                testCaseRepository.findByCodingDetailExerciseId(
                        exerciseId, pageable);

        return PageResponseHelper.toPageResponse(pageData, page);
    }

    public Exercise getExerciseOrThrow(String exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND));
    }

    public TestCase getTestCaseOrThrow(String testCaseId) {
        return testCaseRepository
                .findById(testCaseId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.TESTCASE_NOT_FOUND)
                );
    }
}
