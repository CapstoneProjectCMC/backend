package com.codecampus.submission.service;

import com.codecampus.submission.dto.request.contest.CreateContestRequest;
import com.codecampus.submission.dto.response.contest.MyContestResponse;
import com.codecampus.submission.entity.Contest;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Submission;
import com.codecampus.submission.helper.AuthenticationHelper;
import com.codecampus.submission.helper.ContestHelper;
import com.codecampus.submission.mapper.ContestMapper;
import com.codecampus.submission.repository.ContestRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContestService {

    ContestRepository contestRepository;

    ContestMapper contestMapper;

    AssignmentService assignmentService;
    ContestHelper contestHelper;

    @Transactional
    public Contest createContest(
            CreateContestRequest createContestRequest) {

        Set<Exercise> exercises =
                contestHelper.getExercisesAndCheckExerciseTypeFromCreateContestRequest(
                        createContestRequest);

        // TODO HARDCODE VÌ CHƯA CÓ TỔ CHỨC
        Contest contest = contestMapper.toContestFromCreateContestRequest(
                createContestRequest);
        contest.setOrgId("ĐÂY LÀ ID TỔ CHỨC");
        contest.setExercises(exercises);
        contestRepository.save(contest);

        for (String studentId : createContestRequest.studentIds()) {
            for (Exercise exercise : exercises) {
                assignmentService.assignExercise(
                        exercise.getId(),
                        studentId,
                        createContestRequest.endTime());
            }
        }

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
        }
    }

}
