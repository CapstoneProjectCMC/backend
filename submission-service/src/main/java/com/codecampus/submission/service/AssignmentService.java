package com.codecampus.submission.service;

import com.codecampus.submission.entity.Assignment;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.helper.ExerciseHelper;
import com.codecampus.submission.repository.AssignmentRepository;
import com.codecampus.submission.service.grpc.GrpcQuizClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentService {
    AssignmentRepository assignmentRepository;

    ExerciseHelper exerciseHelper;
    GrpcQuizClient grpcQuizClient;

    @Transactional
    public Assignment assignExercise(
            String exerciseId,
            String studentId,
            Instant dueAt) {
        Exercise exercise =
                exerciseHelper.getExerciseOrThrow(exerciseId);

        Assignment assignment = assignmentRepository
                .findByExerciseIdAndStudentId(exerciseId, studentId)
                .orElseGet(Assignment::new);

        assignment.setExercise(exercise);
        assignment.setStudentId(studentId);
        assignment.setDueAt(dueAt);
        assignment.setCompleted(false);
        assignmentRepository.save(assignment);

        grpcQuizClient.pushAssignment(assignment);
        return assignment;
    }

    /**
     * Đánh dấu completed = true khi submission pass.
     */
    @Transactional
    public void markCompleted(String exerciseId, String studentId) {
        assignmentRepository.findByExerciseIdAndStudentId(exerciseId, studentId)
                .ifPresent(a -> {
                    a.setCompleted(true);
                });
    }
}
