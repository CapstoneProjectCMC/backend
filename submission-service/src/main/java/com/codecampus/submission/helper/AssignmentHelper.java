package com.codecampus.submission.helper;

import com.codecampus.submission.constant.submission.SubmissionStatus;
import com.codecampus.submission.dto.response.assignment.AssignedStudentResponse;
import com.codecampus.submission.dto.response.assignment.MyAssignmentResponse;
import com.codecampus.submission.entity.Assignment;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Submission;
import com.codecampus.submission.repository.SubmissionRepository;
import dtos.UserSummary;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AssignmentHelper {

    SubmissionRepository submissionRepository;

    public MyAssignmentResponse mapAssignmentToMyAssignmentResponse(
            Assignment assignment, String studentId) {

        Score result = getScore(assignment, studentId);
        Instant completedAt =
                getCompletionTimeIfCompleted(assignment, studentId);

        return new MyAssignmentResponse(
                assignment.getId(),
                result.exercise().getId(),
                result.exercise().getTitle(),
                assignment.getDueAt(),
                assignment.isCompleted(),
                completedAt,
                result.bestScore(),
                result.totalPoints(),
                result.exercise().getExerciseType()
        );

    }

    public AssignedStudentResponse mapAssignmentToAssignedStudentResponse(
            Assignment assignment,
            UserSummary studentSummary) {

        Score result = getScore(assignment, assignment.getStudentId());
        Instant completedAt =
                getCompletionTimeIfCompleted(assignment,
                        assignment.getStudentId());

        return new AssignedStudentResponse(
                assignment.getId(),
                studentSummary,
                assignment.getDueAt(),
                assignment.isCompleted(),
                result.bestScore(),
                result.totalPoints(),
                result.exercise().getExerciseType(),
                completedAt
        );
    }

    Score getScore(Assignment assignment, String studentId) {
        Exercise exercise = assignment.getExercise();

        Integer totalPoints = switch (exercise.getExerciseType()) {
            case QUIZ -> exercise.getQuizDetail() != null ?
                    exercise.getQuizDetail().getTotalPoints() : null;
            case CODING -> exercise.getCodingDetail() != null ?
                    exercise.getCodingDetail().getTestCases().size() : null;
        };

        Integer bestScore =
                submissionRepository.findBestScoreFromExercise(
                        exercise.getId(),
                        studentId);
        return new Score(exercise, totalPoints, bestScore);
    }

    Instant getCompletionTimeIfCompleted(
            Assignment assignment, String studentId) {
        if (!assignment.isCompleted()) {
            return null;
        }
        return submissionRepository
                .findFirstByExerciseIdAndUserIdAndStatusOrderBySubmittedAtAsc(
                        assignment.getExercise().getId(),
                        studentId,
                        SubmissionStatus.PASSED)
                .map(Submission::getSubmittedAt)
                .orElse(null);
    }

    private record Score(Exercise exercise, Integer totalPoints,
                         Integer bestScore) {
    }
}
