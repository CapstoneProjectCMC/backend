package com.codecampus.submission.helper;

import com.codecampus.submission.dto.response.assignment.MyAssignmentResponse;
import com.codecampus.submission.entity.Assignment;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.repository.SubmissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AssignmentHelper {

    SubmissionRepository submissionRepository;

    public MyAssignmentResponse mapAssignmentToMyAssignmentResponse(
            Assignment assignment, String studentId) {

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

        return new MyAssignmentResponse(
                assignment.getId(),
                exercise.getId(),
                exercise.getTitle(),
                assignment.getDueAt(),
                assignment.isCompleted(),
                bestScore,
                totalPoints
        );

    }

}
