package com.codecampus.submission.helper;

import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.dto.request.contest.CreateContestRequest;
import com.codecampus.submission.dto.response.contest.MyContestResponse;
import com.codecampus.submission.entity.Contest;
import com.codecampus.submission.entity.ContestRanking;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Submission;
import com.codecampus.submission.entity.data.ContestRankId;
import com.codecampus.submission.exception.AppException;
import com.codecampus.submission.exception.ErrorCode;
import com.codecampus.submission.repository.ContestRankingRepository;
import com.codecampus.submission.repository.SubmissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ContestHelper {

    SubmissionRepository submissionRepository;
    ContestRankingRepository contestRankingRepository;

    ExerciseHelper exerciseHelper;

    public MyContestResponse toMyContestResponseFromContest(
            Contest contest,
            String studentId,
            Instant now) {

        int totalQuestions = contest
                .getExercises()
                .stream()
                .mapToInt(this::questionsOf)
                .sum();

        int totalDuration = contest
                .getExercises()
                .stream()
                .mapToInt(Exercise::getDuration)
                .sum();

        // maxScore = tổng điểm theo loại
        int maxScore = contest
                .getExercises()
                .stream()
                .mapToInt(e -> {
                    if (e.getExerciseType() == ExerciseType.QUIZ &&
                            e.getQuizDetail() != null) {
                        return e.getQuizDetail().getTotalPoints();
                    }
                    // coding: tạm dùng số test case
                    if (e.getExerciseType() == ExerciseType.CODING &&
                            e.getCodingDetail() != null) {
                        return e.getCodingDetail().getTestCases()
                                .size(); // hoặc 100/…
                    }
                    return 0;
                }).sum();


        // tổng điểm đạt được & tổng thời gian
        List<Submission> quizSubmission = submissionRepository
                .findQuizSubmissionByContestAndStudent(
                        contest.getId(), studentId);

        int totalScore = quizSubmission
                .stream()
                .mapToInt(s -> Optional
                        .ofNullable(s.getScore())
                        .orElse(0)
                )
                .sum();

        int totalTime = quizSubmission
                .stream()
                .mapToInt(s -> Optional
                        .ofNullable(s.getTimeTakenSeconds())
                        .orElse(0))
                .sum();

        boolean completed = isContestCompletedForStudent(
                contest, studentId);

        boolean inProgress =
                now.isAfter(contest.getStartTime()) &&
                        now.isBefore(contest.getEndTime());

        return new MyContestResponse(
                contest.getId(), contest.getTitle(),
                contest.getStartTime(), contest.getEndTime(),
                inProgress, completed,
                totalScore, maxScore,
                totalTime,
                totalQuestions, totalDuration);
    }

    public boolean isContestCompletedForStudent(
            Contest contest,
            String studentId) {
        // Completed nếu mọi Assignment của student cho contest đều completed
        return contest
                .getExercises()
                .stream()
                .allMatch(exercise ->
                        exercise.getAssignments()
                                .stream()
                                .anyMatch(assignment ->
                                        assignment.getStudentId()
                                                .equals(studentId) &&
                                                assignment.isCompleted()));
    }

    public void recomputeContestScoreForStudent(
            Contest contest, String studentId) {

        // Best score/time per exercise
        Map<String, Submission> best = bestSubmissionPerExercise(
                contest, studentId);

        int totalScore = best
                .values()
                .stream()
                .mapToInt(s -> Optional
                        .ofNullable(s.getScore())
                        .orElse(0))
                .sum();

        int totalTime = best
                .values()
                .stream()
                .mapToInt(s -> Optional
                        .ofNullable(s.getTimeTakenSeconds())
                        .orElse(0))
                .sum();

        ContestRankId contestRankId = new ContestRankId(
                contest.getId(), studentId);

        ContestRanking contestRanking = contestRankingRepository
                .findById(contestRankId)
                .orElseGet(() -> ContestRanking.builder()
                        .id(contestRankId)
                        .contest(contest)
                        .score(0)
                        .rank(null)
                        .build()
                );
        contestRanking.setScore(totalScore);
        contestRankingRepository.save(contestRanking);

        //FIXME Cập nhật rank (đơn giản: tính lại toàn bộ)
        recalcRanks(contest.getId());
    }

    public Map<String, Submission> bestSubmissionPerExercise(
            Contest contest,
            String studentId) {

        // Lấy mọi submission user cho các exercise của contest
        Set<String> exerciseIds = contest
                .getExercises()
                .stream()
                .map(Exercise::getId)
                .collect(Collectors.toSet());

        List<Submission> submissions = submissionRepository
                .findQuizSubmissionsByStudent(studentId)
                .stream()
                .filter(s -> exerciseIds.contains(
                        s.getExercise().getId()))
                .toList();

        Map<String, Submission> best = new HashMap<>();
        for (Submission submission : submissions) {
            best.merge(
                    submission.getExercise().getId(),
                    submission,
                    (oldSubmission, newSubmission) -> {
                        int oldScore = Optional
                                .ofNullable(oldSubmission.getScore())
                                .orElse(0);
                        int newScore = Optional
                                .ofNullable(newSubmission.getScore())
                                .orElse(0);
                        if (newScore > oldScore) {
                            return newSubmission;
                        } else if (newScore < oldScore) {
                            return oldSubmission;
                        }

                        // Điểm bằng → thời gian ít hơn thắng
                        int oldTime = Optional
                                .ofNullable(oldSubmission.getTimeTakenSeconds())
                                .orElse(Integer.MAX_VALUE);
                        int newTime = Optional
                                .ofNullable(newSubmission.getTimeTakenSeconds())
                                .orElse(Integer.MAX_VALUE);
                        return newTime < oldTime
                                ? newSubmission
                                : oldSubmission;
                    }
            );
        }

        return best;
    }

    public void recalcRanks(String contestId) {

        List<ContestRanking> list = contestRankingRepository
                .findByContestIdOrderByScoreDescRankAsc(contestId);

        // Sort với tiebreaker: totalTime (cần join Submission; đơn giản: không tiebreak)
        list.sort(Comparator.comparing(ContestRanking::getScore)
                .reversed());
        int r = 1;
        for (ContestRanking contestRanking : list) {
            contestRanking.setRank(r++);
        }
        contestRankingRepository.saveAll(list);
    }

    public int questionsOf(Exercise e) {
        return switch (e.getExerciseType()) {
            case QUIZ -> e.getQuizDetail() != null ?
                    e.getQuizDetail().getNumQuestions() : 0;
            case CODING -> e.getCodingDetail() != null ?
                    e.getCodingDetail().getTestCases().size() : 0;
        };
    }

    public Set<Exercise> getExercisesAndCheckExerciseTypeFromCreateContestRequest(
            CreateContestRequest createContestRequest) {
        Set<Exercise> exercises = createContestRequest
                .exerciseIds()
                .stream()
                .map(exerciseHelper::getExerciseOrThrow)
                .collect(toSet());

        checkSameExerciseTypeFromExercises(exercises);
        return exercises;
    }

    public void checkSameExerciseTypeFromExercises(
            Set<Exercise> exercises) {
        // Validate cùng loại
        ExerciseType exerciseType = exercises
                .iterator()
                .next()
                .getExerciseType();
        boolean sameType = exercises
                .stream()
                .allMatch(e -> e.getExerciseType() == exerciseType);

        if (!sameType) {
            throw new AppException(ErrorCode.EXERCISE_TYPE);
        }
    }

    public int maxScoreOfContest(Contest contest) {
        return contest
                .getExercises()
                .stream()
                .mapToInt(this::questionsOf)
                .sum();
    }
}
