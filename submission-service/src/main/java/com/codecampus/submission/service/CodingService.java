package com.codecampus.submission.service;

import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.dto.request.coding.AddCodingDetailRequest;
import com.codecampus.submission.dto.request.coding.TestCaseDto;
import com.codecampus.submission.dto.request.coding.UpdateCodingDetailRequest;
import com.codecampus.submission.dto.request.coding.UpdateTestCaseRequest;
import com.codecampus.submission.entity.CodingDetail;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.TestCase;
import com.codecampus.submission.exception.AppException;
import com.codecampus.submission.exception.ErrorCode;
import com.codecampus.submission.mapper.CodingMapper;
import com.codecampus.submission.mapper.TestCaseMapper;
import com.codecampus.submission.repository.CodingDetailRepository;
import com.codecampus.submission.repository.ExerciseRepository;
import com.codecampus.submission.repository.TestCaseRepository;
import com.codecampus.submission.service.grpc.GrpcCodingClient;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Optional;

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

    // HACK HARDCODE ĐỂ FIX BUG BÊN DƯỚI TẠM THỜI
    // @PersistenceContext
    // EntityManager em;


    @Transactional
    public void addCodingDetail(
            String exerciseId,
            AddCodingDetailRequest addCodingRequest) {

        Exercise exercise = getExerciseOrThrow(exerciseId);
        Assert.isTrue(
                exercise.getExerciseType() == ExerciseType.CODING,
                "Exercise không phải CODE"
        );

        if (exercise.getCodingDetail() != null) {
            throw new AppException(ErrorCode.EXERCISE_TYPE);
        }

        CodingDetail codingDetail = new CodingDetail();
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

        codingDetail.setTopic(addCodingRequest.topic());
        codingDetail.setAllowedLanguages(addCodingRequest.allowedLanguages());
        codingDetail.setInput(addCodingRequest.input());
        codingDetail.setOutput(addCodingRequest.output());
        codingDetail.setConstraintText(addCodingRequest.constraintText());
        codingDetail.setTimeLimit(addCodingRequest.timeLimit());
        codingDetail.setMemoryLimit(addCodingRequest.memoryLimit());
        codingDetail.setMaxSubmissions(addCodingRequest.maxSubmissions());
        codingDetail.setCodeTemplate(addCodingRequest.codeTemplate());
        codingDetail.setSolution(addCodingRequest.solution());

        addCodingRequest.testCases().forEach(tcDto -> {
            TestCase testCase = testCaseMapper.toTestCase(tcDto);
            testCase.setCodingDetail(codingDetail);
            codingDetail.getTestCases().add(testCase);
        });

//        exercise.setCodingDetail(codingDetail);
        codingDetailRepository.save(codingDetail);

        // HACK HARDCODE ĐỂ FIX CÁI BUG ID TRÊN TẠM THỜI
        // em.persist(codingDetail);

        grpcCodingClient.pushCodingDetail(codingDetail);
    }

    @Transactional
    public void updateCodingDetail(
            String exerciseId,
            UpdateCodingDetailRequest updateCodingDetailRequest) {

        Exercise exercise = getExerciseOrThrow(exerciseId);
        CodingDetail codingDetail =
                Optional.ofNullable(exercise.getCodingDetail())
                        .orElseThrow(() -> new AppException(
                                ErrorCode.CODING_DETAIL_NOT_FOUND));

        codingMapper.patchUpdateCodingDetailRequest(codingDetail,
                updateCodingDetailRequest);
        CodingDetail saved = codingDetailRepository.save(codingDetail);

        grpcCodingClient.pushCodingDetail(saved);
    }

    @Transactional
    public void addTestCase(
            String exerciseId,
            TestCaseDto testCaseDto) throws BadRequestException {

        Exercise exercise = getExerciseOrThrow(exerciseId);
        CodingDetail codingDetail = Optional
                .ofNullable(exercise.getCodingDetail())
                .orElseThrow(
                        () -> new BadRequestException("Chưa có CodingDetail")
                );

        TestCase testCase = testCaseMapper.toTestCase(testCaseDto);
        testCase.setCodingDetail(codingDetail);
        codingDetail.getTestCases().add(testCase);

        TestCase testCaseSaved = testCaseRepository.save(testCase);

        grpcCodingClient.pushTestCase(testCaseSaved);
    }

    @Transactional
    public void updateTestCase(
            String testCaseId,
            UpdateTestCaseRequest request) {

        TestCase tc = testCaseRepository.findById(testCaseId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.TESTCASE_NOT_FOUND));

        testCaseMapper.patch(tc, request);
        TestCase saved = testCaseRepository.save(tc);

        grpcCodingClient.pushTestCase(saved);
    }

    Exercise getExerciseOrThrow(String exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND));
    }
}
