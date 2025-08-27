package com.codecampus.submission.service;

import com.codecampus.submission.dto.request.contest.CreateContestRequest;
import com.codecampus.submission.dto.response.contest.MyContestResponse;
import com.codecampus.submission.entity.Contest;
import com.codecampus.submission.entity.ContestRanking;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Submission;
import com.codecampus.submission.entity.data.ContestRankId;
import com.codecampus.submission.exception.AppException;
import com.codecampus.submission.exception.ErrorCode;
import com.codecampus.submission.helper.AuthenticationHelper;
import com.codecampus.submission.helper.ContestHelper;
import com.codecampus.submission.mapper.ContestMapper;
import com.codecampus.submission.repository.ContestRankingRepository;
import com.codecampus.submission.repository.ContestRepository;
import com.codecampus.submission.service.kafka.ContestEventProducer;
import com.codecampus.submission.service.kafka.ContestStatusEventProducer;
import dtos.ContestStatusDto;
import dtos.ContestSummary;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContestService {

  ContestRepository contestRepository;
  ContestRankingRepository contestRankingRepository;
  ContestMapper contestMapper;
  AssignmentService assignmentService;

  ContestEventProducer contestEventProducer;
  ContestStatusEventProducer contestStatusEventProducer;

  ContestHelper contestHelper;

  @Transactional
  public Contest createContest(
      CreateContestRequest createContestRequest) {

    Set<Exercise> exercises =
        contestHelper.getExercisesAndCheckExerciseTypeFromCreateContestRequest(
            createContestRequest);

    Contest contest = contestMapper.toContestFromCreateContestRequest(
        createContestRequest);
    contest.setOrgId("ĐÂY LÀ ID TỔ CHỨC");
    contest.setExercises(exercises);
    contestRepository.save(contest);

    // Assign cho từng học sinh
    for (String studentId : createContestRequest.studentIds()) {
      for (Exercise exercise : exercises) {
        assignmentService.assignExercise(
            exercise.getId(),
            studentId,
            createContestRequest.endTime());
      }
    }

    // Phát event để profile-service đồng bộ
    contestEventProducer.publishCreated(contest);

    return contest;
  }


  @Transactional(readOnly = true)
  public List<MyContestResponse> getMyContests() {

    String studentId = AuthenticationHelper.getMyUserId();

    List<Contest> contests =
        contestRepository.findAllContestsForStudent(studentId);

    return contests
        .stream()
        .map(contest -> contestHelper.toMyContestResponseFromContest(
            contest, studentId, Instant.now()))
        .sorted(Comparator.comparing(MyContestResponse::startTime))
        .toList();
  }

  @Transactional
  public void updateRankingOnSubmission(
      Submission submission) {
    // Lấy contests chứa exercise này
    List<Contest> contests = contestRepository.findAllContestsByExerciseId(
        submission.getExercise().getId());
    for (Contest contest : contests) {
      contestHelper.recomputeContestScoreForStudent(contest,
          submission.getUserId());

      ContestStatusDto contestStatusDto = getContestStatus(
          contest.getId(),
          submission.getUserId(),
          Instant.now()
      );

      contestStatusEventProducer.publishUpsert(contestStatusDto);

    }
  }

  public ContestSummary getContestSummary(String contestId) {
    Contest contest = contestRepository.findById(contestId)
        .orElseThrow(() -> new AppException(ErrorCode.CONTEST_NOT_FOUND));
    return ContestSummary.builder()
        .id(contest.getId())
        .title(contest.getTitle())
        .startTime(contest.getStartTime())
        .endTime(contest.getEndTime())
        .rankPublic(contest.isRankPublic())
        .orgId(contest.getOrgId())
        .build();
  }

  @Transactional(readOnly = true)
  public ContestStatusDto getContestStatus(
      String contestId, String studentId,
      Instant now) {

    Contest contest = contestRepository
        .findById(contestId)
        .orElse(null);

    if (contest == null) {
      return null;
    }

    String state = now.isBefore(contest.getStartTime()) ? "REGISTERED"
        : (now.isAfter(contest.getEndTime()) ? "FINISHED" : "IN_PROGRESS");

    Optional<ContestRanking> rankOpt =
        contestRankingRepository.findById(
            new ContestRankId(contestId, studentId));

    Integer rank = rankOpt
        .map(ContestRanking::getRank)
        .orElse(null);

    Double score = rankOpt.map(
            r -> r.getScore() == null ? null : r.getScore().doubleValue())
        .orElse(null);

    Instant updatedAt = rankOpt.map(ContestRanking::getUpdatedAt).orElse(null);

    return new ContestStatusDto(
        contestId,
        studentId,
        state,
        rank,
        score,
        updatedAt);
  }
}
