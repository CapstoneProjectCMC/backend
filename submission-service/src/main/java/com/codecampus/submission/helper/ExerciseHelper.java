package com.codecampus.submission.helper;

import com.codecampus.submission.dto.response.coding.coding_detail.CodingDetailSliceDetailResponse;
import com.codecampus.submission.dto.response.coding.coding_detail.ExerciseCodingDetailResponse;
import com.codecampus.submission.dto.response.quiz.ExerciseQuizResponse;
import com.codecampus.submission.dto.response.quiz.quiz_detail.ExerciseQuizDetailResponse;
import com.codecampus.submission.dto.response.quiz.quiz_detail.QuizDetailSliceDetailResponse;
import com.codecampus.submission.entity.CodingDetail;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.QuizDetail;
import com.codecampus.submission.exception.AppException;
import com.codecampus.submission.exception.ErrorCode;
import com.codecampus.submission.repository.ExerciseRepository;
import dtos.UserSummary;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ExerciseHelper {

    ExerciseRepository exerciseRepository;

    public Exercise getExerciseOrThrow(
            String exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
                );
    }

    public void markExerciseDeletedRecursively(
            Exercise exercise,
            String by) {
        exercise.markDeleted(by);

        if (exercise.getCodingDetail() != null) {
            CodingDetail codingDetail = exercise.getCodingDetail();
            codingDetail.markDeleted(by);
            codingDetail.getTestCases()
                    .forEach(tc -> tc.markDeleted(by));
        }
        if (exercise.getQuizDetail() != null) {
            QuizDetail quizDetail = exercise.getQuizDetail();
            quizDetail.markDeleted(by);
            quizDetail.getQuestions()
                    .forEach(question -> {
                        question.markDeleted(by);
                        question.getOptions()
                                .forEach(option -> option.markDeleted(by));
                    });
        }
    }

    public ExerciseQuizResponse toExerciseQuizResponseFromExerciseAndUserSummary(
            Exercise e,
            UserSummary summary) {
        return ExerciseQuizResponse.builder()
                .id(e.getId())
                .user(summary)
                .title(e.getTitle())
                .description(e.getDescription())
                .difficulty(e.getDifficulty())
                .exerciseType(e.getExerciseType())
                .orgId(e.getOrgId())
                .cost(e.getCost())
                .freeForOrg(e.isFreeForOrg())
                .tags(e.getTags())
                .visibility(e.isVisibility())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public ExerciseQuizDetailResponse toExerciseQuizDetailResponseFromExerciseQuizDetailSliceDetailResponseAndUserSummary(
            Exercise e,
            QuizDetailSliceDetailResponse qSlice,
            UserSummary summary) {
        return ExerciseQuizDetailResponse.builder()
                .id(e.getId())
                .user(summary)
                .title(e.getTitle())
                .description(e.getDescription())
                .exerciseType(e.getExerciseType())
                .difficulty(e.getDifficulty())
                .orgId(e.getOrgId())
                .active(e.isActive())
                .cost(e.getCost())
                .freeForOrg(e.isFreeForOrg())
                .startTime(e.getStartTime())
                .endTime(e.getEndTime())
                .duration(e.getDuration())
                .allowDiscussionId(e.getAllowDiscussionId())
                .resourceIds(e.getResourceIds())
                .tags(e.getTags())
                .allowAiQuestion(e.isAllowAiQuestion())
                .visibility(e.isVisibility())
                .quizDetail(qSlice)
                .createdBy(e.getCreatedBy())
                .createdAt(e.getCreatedAt())
                .updatedBy(e.getUpdatedBy())
                .updatedAt(e.getUpdatedAt())
                .deletedBy(e.getDeletedBy())
                .deletedAt(e.getDeletedAt())
                .build();
    }

    public ExerciseCodingDetailResponse toExerciseCodingDetailResponseFromExerciseCodingDetailSliceDetailResponseAndUserSummary(
            Exercise e,
            CodingDetailSliceDetailResponse slice,
            UserSummary summary) {
        return ExerciseCodingDetailResponse.builder()
                .id(e.getId())
                .user(summary)
                .title(e.getTitle())
                .description(e.getDescription())
                .exerciseType(e.getExerciseType())
                .difficulty(e.getDifficulty())
                .orgId(e.getOrgId())
                .active(e.isActive())
                .cost(e.getCost())
                .freeForOrg(e.isFreeForOrg())
                .startTime(e.getStartTime())
                .endTime(e.getEndTime())
                .duration(e.getDuration())
                .allowDiscussionId(e.getAllowDiscussionId())
                .resourceIds(e.getResourceIds())
                .tags(e.getTags())
                .allowAiQuestion(e.isAllowAiQuestion())
                .visibility(e.isVisibility())
                .codingDetail(slice)
                .createdBy(e.getCreatedBy())
                .createdAt(e.getCreatedAt())
                .updatedBy(e.getUpdatedBy())
                .updatedAt(e.getUpdatedAt())
                .deletedBy(e.getDeletedBy())
                .deletedAt(e.getDeletedAt())
                .build();
    }
}
