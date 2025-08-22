package com.codecampus.submission.service;

import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.dto.request.assignment.BulkAssignExerciseRequest;
import com.codecampus.submission.entity.Assignment;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.helper.ExerciseHelper;
import com.codecampus.submission.repository.AssignmentRepository;
import com.codecampus.submission.service.grpc.GrpcCodingClient;
import com.codecampus.submission.service.grpc.GrpcQuizClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AssignmentService {
    AssignmentRepository assignmentRepository;

    ExerciseHelper exerciseHelper;
    GrpcQuizClient grpcQuizClient;
    GrpcCodingClient grpcCodingClient;

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


        if (exercise.getExerciseType() == ExerciseType.QUIZ) {
            grpcQuizClient.pushAssignment(assignment);
        } else if (exercise.getExerciseType() == ExerciseType.CODING) {
            grpcCodingClient.pushAssignment(assignment);
        }
        return assignment;
    }

    @Transactional
    public List<Assignment> assignExerciseToMany(
            String exerciseId,
            BulkAssignExerciseRequest bulkAssignExerciseRequest) {
        List<String> studentIds = bulkAssignExerciseRequest.studentIds()
                .stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(studentId -> !studentId.isEmpty())
                .distinct()
                .toList();
        if (studentIds.isEmpty()) {
            return List.of();
        }

        Exercise exercise = exerciseHelper.getExerciseOrThrow(exerciseId);

        // Lấy các assignment hiện có
        Map<String, Assignment> existingByStudent = assignmentRepository
                .findByExerciseIdAndStudentIdIn(exerciseId, studentIds)
                .stream()
                .collect(Collectors.toMap(Assignment::getStudentId,
                        Function.identity()));

        // Upsert
        List<Assignment> assignmentList = new ArrayList<>(studentIds.size());
        for (String studentId : studentIds) {
            Assignment assignment =
                    existingByStudent.getOrDefault(studentId,
                            Assignment.builder().build());
            assignment.setExercise(exercise);
            assignment.setStudentId(studentId);
            assignment.setDueAt(bulkAssignExerciseRequest.dueAt());
            assignment.setCompleted(false);
            assignmentList.add(assignment);
        }

        List<Assignment> assignmentListSaved =
                assignmentRepository.saveAll(assignmentList);

        assignmentListSaved.forEach(
                assignment -> pushAssignmentToChildService(
                        exercise, assignment));

        return assignmentListSaved;
    }

    /**
     * Đánh dấu completed = true khi submission pass.
     */
    @Transactional
    public void markCompleted(
            String exerciseId,
            String studentId) {
        assignmentRepository.findByExerciseIdAndStudentId(exerciseId, studentId)
                .ifPresent(a -> {
                    a.setCompleted(true);
                    // Đẩy trạng thái mới sang service con để đồng bộ (idempotent)
                    pushAssignmentToChildService(a.getExercise(), a);
                });
    }


    public void pushAssignmentToChildService(
            Exercise exercise,
            Assignment assignment) {
        if (exercise.getExerciseType() == ExerciseType.QUIZ) {
            grpcQuizClient.pushAssignment(assignment);
        } else if (exercise.getExerciseType() == ExerciseType.CODING) {
            grpcCodingClient.pushAssignment(assignment);
        }
    }
}
