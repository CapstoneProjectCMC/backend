package com.codecampus.submission.service;

import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.dto.request.assignment.BulkAssignExerciseRequest;
import com.codecampus.submission.entity.Assignment;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.helper.AuthenticationHelper;
import com.codecampus.submission.helper.ExerciseHelper;
import com.codecampus.submission.repository.AssignmentRepository;
import com.codecampus.submission.service.grpc.GrpcCodingClient;
import com.codecampus.submission.service.grpc.GrpcQuizClient;
import com.codecampus.submission.service.kafka.ExerciseStatusEventProducer;
import com.codecampus.submission.service.kafka.NotificationEventProducer;
import dtos.ExerciseStatusDto;
import events.notification.NotificationEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
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
public class AssignmentService {
  AssignmentRepository assignmentRepository;

  ExerciseHelper exerciseHelper;

  ExerciseStatusEventProducer exerciseStatusEventProducer;
  NotificationEventProducer notificationEventProducer;

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
    assignmentRepository.saveAndFlush(assignment);

    pushAssignmentToChildService(exercise, assignment);

    ExerciseStatusDto exerciseStatusDto = new ExerciseStatusDto(
        exerciseId, studentId,
        /* created   */ false,
        /* completed */ false,
        /* completedAt */ null,
        /* attempts   */ null,
        /* bestScore  */ null,
        /* totalPts   */ null);
    exerciseStatusEventProducer.publishUpsert(exerciseStatusDto);

    NotificationEvent evt = NotificationEvent.builder()
        .channel("SOCKET") // hoặc "EMAIL", "PUSH" nếu sau này mở rộng
        .recipient(studentId) // userId đích
        .templateCode("ASSIGNMENT_ASSIGNED") // code template
        .subject("Bạn được giao bài tập mới")
        .body("Bạn vừa được giao bài: " + exercise.getTitle())
        .param(Map.of(
            "exerciseId", exercise.getId(),
            "exerciseTitle", exercise.getTitle(),
            "dueAt", dueAt
        ))
        .build();
    notificationEventProducer.publish(evt);

    return assignment;
  }

  @Transactional
  public void softDeleteAssignment(
      String exerciseId,
      String studentId) {
    String by = AuthenticationHelper.getMyUsername();

    assignmentRepository.findByExerciseIdAndStudentId(exerciseId, studentId)
        .ifPresent(a -> {
          a.markDeleted(by);
          assignmentRepository.save(a);
          pushAssignmentDeleteToChildService(a);
        });
  }

  @Transactional
  public void bulkSoftDeleteAssignments(
      String exerciseId, Set<String> studentIds) {

    String by = AuthenticationHelper.getMyUsername();

    if (studentIds == null || studentIds.isEmpty()) {
      return;
    }

    List<Assignment> assigmentList = assignmentRepository
        .findByExerciseIdAndStudentIdIn(exerciseId,
            studentIds.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty()).distinct().toList()
        );

    assigmentList.forEach(assignment -> {
      assignment.markDeleted(by);
      pushAssignmentDeleteToChildService(assignment);
    });
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
        assignmentRepository.saveAllAndFlush(assignmentList);

    assignmentListSaved.forEach(
        assignment -> {
          pushAssignmentToChildService(
              exercise, assignment);

          ExerciseStatusDto exerciseStatusDto = new ExerciseStatusDto(
              exerciseId, assignment.getStudentId(),
              /* created   */ false,
              /* completed */ false,
              /* completedAt */ null,
              /* attempts   */ null,
              /* bestScore  */ null,
              /* totalPts   */ null);
          exerciseStatusEventProducer.publishUpsert(exerciseStatusDto);

          NotificationEvent evt = NotificationEvent.builder()
              .channel("SOCKET") // hoặc "EMAIL", "PUSH" nếu sau này mở rộng
              .recipient(assignment.getStudentId()) // userId đích
              .templateCode("ASSIGNMENT_ASSIGNED") // code template
              .subject("Bạn được giao bài tập mới")
              .body("Bạn vừa được giao bài: " + exercise.getTitle())
              .param(Map.of(
                  "exerciseId", exercise.getId(),
                  "exerciseTitle", exercise.getTitle(),
                  "dueAt", assignment.getDueAt()
              ))
              .build();
          notificationEventProducer.publish(evt);
        });

    return assignmentListSaved;
  }

  /**
   * Đánh dấu completed = true khi submission pass.
   */
  @Transactional
  public void markCompleted(
      String exerciseId,
      String studentId) {

    Exercise exercise = exerciseHelper.getExerciseOrThrow(exerciseId);

    assignmentRepository.findByExerciseIdAndStudentId(exerciseId, studentId)
        .ifPresent(a -> {
          a.setCompleted(true);
          // Đẩy trạng thái mới sang service con để đồng bộ (idempotent)
          pushAssignmentToChildService(a.getExercise(), a);

          ExerciseStatusDto exerciseStatusDto =
              new ExerciseStatusDto(
                  exerciseId,
                  studentId,
                  /* created */ studentId.equals(a.getExercise().getUserId()),
                  /* completed */ true,
                  a.getUpdatedAt(), // completedAt
                  /* attempts */ null,
                  /* bestScore */ null,
                  /* totalPts */ null
              );

          exerciseStatusEventProducer.publishUpsert(exerciseStatusDto);
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

  public void pushAssignmentDeleteToChildService(Assignment assignment) {
    Exercise e = assignment.getExercise();
    if (e.getExerciseType() == ExerciseType.QUIZ) {
      grpcQuizClient.softDeleteAssignment(assignment.getId());
    } else if (e.getExerciseType() == ExerciseType.CODING) {
      grpcCodingClient.softDeleteAssignment(assignment.getId());
    }
  }
}
