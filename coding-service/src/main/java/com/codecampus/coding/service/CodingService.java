package com.codecampus.coding.service;

import com.codecampus.coding.entity.Assignment;
import com.codecampus.coding.entity.CodingExercise;
import com.codecampus.coding.entity.TestCase;
import com.codecampus.coding.exception.AppException;
import com.codecampus.coding.exception.ErrorCode;
import com.codecampus.coding.grpc.AddCodingDetailRequest;
import com.codecampus.coding.grpc.AddTestCaseRequest;
import com.codecampus.coding.grpc.AssignmentDto;
import com.codecampus.coding.grpc.CodingExerciseDto;
import com.codecampus.coding.grpc.CreateCodingExerciseRequest;
import com.codecampus.coding.grpc.LoadCodingResponse;
import com.codecampus.coding.grpc.TestCaseDto;
import com.codecampus.coding.grpc.UpsertAssignmentRequest;
import com.codecampus.coding.helper.AuthenticationHelper;
import com.codecampus.coding.helper.CodingHelper;
import com.codecampus.coding.mapper.AssignmentMapper;
import com.codecampus.coding.mapper.CodingMapper;
import com.codecampus.coding.repository.AssignmentRepository;
import com.codecampus.coding.repository.CodingExerciseRepository;
import com.codecampus.coding.service.redis.LoadCodingCacheService;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CodingService {

  CodingExerciseRepository codingExerciseRepository;
  AssignmentRepository assignmentRepository;

  RedissonClient redisson;

  AssignmentMapper assignmentMapper;
  CodingMapper codingMapper;

  LoadCodingCacheService loadCodingCacheService;

  CodingHelper codingHelper;

  @Transactional
  public void createCodingExercise(
      CreateCodingExerciseRequest createCodingRequest) {

    CodingExerciseDto exerciseDto = createCodingRequest.getExercise();
    CodingExercise codingExercise = codingExerciseRepository
        .findById(exerciseDto.getId())
        .orElseGet(CodingExercise::new);

    codingMapper.patchCodingExerciseDtoToCodingExercise(
        exerciseDto, codingExercise);
    codingExerciseRepository.save(codingExercise);
  }

  @Transactional
  public void addCodingDetail(
      AddCodingDetailRequest addCodingRequest) {

    CodingExercise coding =
        codingHelper.findCodingOrThrow(
            addCodingRequest.getExerciseId());

    codingMapper.patchCodingDetailDtoToCodingExercise(
        coding, addCodingRequest.getCodingDetail());

    addCodingRequest.getCodingDetail().getTestcasesList()
        .forEach(testCaseDto -> {
          TestCase testCase = coding.getTestCases()
              .stream()
              .filter(tc -> tc.getId()
                  .equals(testCaseDto.getId()))
              .findFirst()
              .orElseGet(() -> {
                TestCase newTestCase =
                    codingMapper.toTestCaseFromTestCaseDto(
                        testCaseDto
                    );

                newTestCase.setCoding(coding);
                coding.getTestCases().add(newTestCase);
                return newTestCase;
              });

          codingMapper.patchTestCaseDtoToTestCase(
              testCaseDto, testCase);
        });

    codingExerciseRepository.saveAndFlush(coding);

    loadCodingCacheService.refresh(coding.getId());
  }

  @Transactional
  public void softDeleteExercise(String exerciseId) {
    CodingExercise codingExercise =
        codingHelper.findCodingOrThrow(exerciseId);
    String by = AuthenticationHelper.getMyUsername();
    codingExercise.markDeleted(by);
    codingExercise.getTestCases().forEach(tc -> {
      tc.markDeleted(by);
    });
    codingExerciseRepository.save(codingExercise);

    loadCodingCacheService.refresh(exerciseId);
  }

  @Transactional
  public void addTestCase(
      AddTestCaseRequest addTestCaseRequest) {
    CodingExercise coding =
        codingHelper.findCodingOrThrow(
            addTestCaseRequest.getExerciseId());
    TestCaseDto testCaseDto = addTestCaseRequest.getTestCase();

    TestCase testCase = coding.findTestCaseById(testCaseDto.getId())
        .orElseGet(() -> {
          TestCase tc = codingMapper.toTestCaseFromTestCaseDto(
              testCaseDto);
          tc.setCoding(coding);
          coding.getTestCases().add(tc);
          return tc;
        });

    codingMapper.patchTestCaseDtoToTestCase(testCaseDto, testCase);

    codingExerciseRepository.save(coding);

    loadCodingCacheService.refresh(addTestCaseRequest.getExerciseId());
  }

  @Transactional
  public void softDeleteTestCase(
      String exerciseId,
      String testCaseId) {

    CodingExercise codingExercise = codingHelper
        .findCodingOrThrow(exerciseId);
    codingExercise.findTestCaseById(testCaseId).ifPresent(testCase -> {
      testCase.markDeleted(AuthenticationHelper.getMyUsername());
    });

    loadCodingCacheService.refresh(exerciseId);
  }

  @Transactional(readOnly = true)
  public LoadCodingResponse loadCoding(
      String exerciseId) {

    String userId = AuthenticationHelper.getMyUserId();
    String username = AuthenticationHelper.getMyUsername();
    Set<String> roles = Set.copyOf(AuthenticationHelper.getMyRoles());
    boolean teacher = AuthenticationHelper.getMyRoles().contains("TEACHER");

    /* ---------- Thử lấy từ cache ---------- */
    LoadCodingResponse loadCodingCached = loadCodingCacheService
        .get(exerciseId);
    if (loadCodingCached != null && codingHelper
        .hasAccessOnLoadCodingResponse(
            loadCodingCached,
            userId, username, teacher)) {
      return loadCodingCached;
    }

    /* ---------- Stampede lock ---------- */
    RLock lock = redisson.getLock("lock:coding:" + exerciseId);
    try {
      // Chặn stampede: chỉ 1 thread/node truy DB
      if (lock.tryLock(2, 10, TimeUnit.SECONDS)) {
        // Re-check sau khi có lock
        loadCodingCached = loadCodingCacheService.get(exerciseId);
        if (loadCodingCached != null) {
          return loadCodingCached;
        }

        // Truy DB -> build response
        CodingExercise coding =
            codingHelper.findCodingOrThrow(exerciseId);

        if (!codingHelper.hasAccessOnCodingExercise(
            coding, userId,
            username, teacher)) {
          throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        LoadCodingResponse rsp =
            codingMapper.toLoadCodingResponseFromCodingExercise(
                coding);

        // Ghi cache
        loadCodingCacheService.put(exerciseId, rsp);
        return rsp;
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      if (lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }

    // Fallback (không lấy được lock)
    CodingExercise coding = codingHelper.findCodingOrThrow(exerciseId);

    if (!codingHelper.hasAccessOnCodingExercise(
        coding, userId, username, teacher)) {
      throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    return codingMapper.toLoadCodingResponseFromCodingExercise(
        coding);
  }

  @Transactional
  public void upsertAssignment(
      UpsertAssignmentRequest request) {
    AssignmentDto assignmentDto = request.getAssignment();
    Assignment assignment = assignmentRepository
        .findById(assignmentDto.getId())
        .orElseGet(Assignment::new);

    assignmentMapper.patchAssignmentDtoToAssignment(
        assignmentDto,
        assignment
    );
    assignmentRepository.save(assignment);
  }

  @Transactional
  public void softDeleteAssignment(
      String assignmentId) {
    Assignment assignment = assignmentRepository
        .findById(assignmentId)
        .orElseThrow(
            () -> new AppException(ErrorCode.ASSIGNMENT_NOT_FOUND));
    String by = AuthenticationHelper.getMyUsername();
    assignment.markDeleted(by);
    assignmentRepository.save(assignment);
  }

}
