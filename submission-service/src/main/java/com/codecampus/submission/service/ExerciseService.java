package com.codecampus.submission.service;

import com.codecampus.submission.constant.sort.SortField;
import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.dto.common.PageResponse;
import com.codecampus.submission.dto.request.CreateExerciseRequest;
import com.codecampus.submission.dto.request.UpdateExerciseRequest;
import com.codecampus.submission.dto.request.quiz.CreateQuizExerciseRequest;
import com.codecampus.submission.dto.response.quiz.ExerciseQuizResponse;
import com.codecampus.submission.dto.response.quiz.quiz_detail.ExerciseQuizDetailResponse;
import com.codecampus.submission.dto.response.quiz.quiz_detail.QuizDetailSliceDetailResponse;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Submission;
import com.codecampus.submission.grpc.CreateQuizSubmissionRequest;
import com.codecampus.submission.grpc.QuizSubmissionDto;
import com.codecampus.submission.helper.AuthenticationHelper;
import com.codecampus.submission.helper.ExerciseHelper;
import com.codecampus.submission.helper.PageResponseHelper;
import com.codecampus.submission.helper.QuizHelper;
import com.codecampus.submission.helper.SortHelper;
import com.codecampus.submission.mapper.ExerciseMapper;
import com.codecampus.submission.mapper.SubmissionMapper;
import com.codecampus.submission.repository.AssignmentRepository;
import com.codecampus.submission.repository.ExerciseRepository;
import com.codecampus.submission.repository.QuestionRepository;
import com.codecampus.submission.repository.SubmissionRepository;
import com.codecampus.submission.service.grpc.GrpcCodingClient;
import com.codecampus.submission.service.grpc.GrpcQuizClient;
import com.codecampus.submission.service.kafka.ExerciseEventProducer;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseService {
    ExerciseRepository exerciseRepository;
    QuestionRepository questionRepository;
    SubmissionRepository submissionRepository;
    AssignmentRepository assignmentRepository;

    ContestService contestService;
    AssignmentService assignmentService;
    QuizService quizService;

    GrpcQuizClient grpcQuizClient;
    GrpcCodingClient grpcCodingClient;
    ExerciseEventProducer exerciseEventProducer;

    ExerciseMapper exerciseMapper;
    SubmissionMapper submissionMapper;

    QuizHelper quizHelper;
    ExerciseHelper exerciseHelper;


    @Transactional
    public Exercise createExercise(
            CreateExerciseRequest request,
            boolean returnExercise) {
        Exercise exercise = exerciseRepository
                .save(exerciseMapper.toExerciseFromCreateExerciseRequest(
                        request, AuthenticationHelper.getMyUserId()));
        if (exercise.getExerciseType() == ExerciseType.QUIZ) {
            grpcQuizClient.pushExercise(exercise);
        } else if (exercise.getExerciseType() == ExerciseType.CODING) {
            grpcCodingClient.pushExercise(exercise);
        }

        exerciseEventProducer.publishCreatedExerciseEvent(exercise);

        if (returnExercise) {
            return exercise;
        }
        return null;
    }

    @Transactional
    public Exercise createQuizExercise(
            CreateQuizExerciseRequest request,
            boolean returnExercise) {
        Exercise exercise =
                createExercise(
                        request.createExerciseRequest(),
                        true
                );
        exerciseRepository.saveAndFlush(exercise);

        quizService.addQuizDetail(
                exercise.getId(),
                request.addQuizDetailRequest(),
                false);

        if (returnExercise) {
            return exercise;
        }
        return null;
    }


    @Transactional
    public void createQuizSubmission(
            CreateQuizSubmissionRequest request) {
        QuizSubmissionDto quizSubmissionDto = request.getSubmission();

        Exercise exercise = exerciseHelper
                .getExerciseOrThrow(quizSubmissionDto.getExerciseId());

        Submission submission = submissionMapper
                .toSubmissionFromQuizSubmissionDto(
                        quizSubmissionDto,
                        exercise,
                        questionRepository
                );
        submissionRepository.save(submission);

        // Set Complete
        if (quizSubmissionDto.getScore() >=
                quizSubmissionDto.getTotalPoints()) {
            assignmentRepository
                    .findByExerciseIdAndStudentId(
                            quizSubmissionDto.getExerciseId(),
                            quizSubmissionDto.getStudentId())
                    .ifPresent(a -> a.setCompleted(true));

            assignmentService.markCompleted(
                    quizSubmissionDto.getExerciseId(),
                    quizSubmissionDto.getStudentId()
            );
        }

        // Cập nhật xếp hạng contest
        contestService.updateRankingOnSubmission(submission);
    }

    @Transactional
    public void updateExercise(
            String id, UpdateExerciseRequest request) {
        Exercise exercise = exerciseHelper
                .getExerciseOrThrow(id);
        exerciseMapper.patchUpdateExerciseRequestToExercise(request, exercise);

        if (exercise.getExerciseType() == ExerciseType.QUIZ) {
            grpcQuizClient.pushExercise(exercise);
        } else if (exercise.getExerciseType() == ExerciseType.CODING) {
            grpcCodingClient.pushExercise(exercise);
        }
        exerciseEventProducer.publishUpdatedExerciseEvent(exercise);
    }

    @Transactional
    public void softDeleteExercise(String exerciseId) {
        Exercise exercise = exerciseHelper
                .getExerciseOrThrow(exerciseId);
        String by = AuthenticationHelper.getMyUsername();
        exerciseHelper.markExerciseDeletedRecursively(exercise, by);
        exerciseRepository.save(exercise);

        exerciseEventProducer.publishDeletedExerciseEvent(exercise);
        if (exercise.getExerciseType() ==
                ExerciseType.QUIZ) {
            grpcQuizClient.softDeleteExercise(exerciseId);
        } else if (exercise.getExerciseType()
                == ExerciseType.CODING) {
            grpcCodingClient.softDeleteExercise(exerciseId);
        }
    }

    public PageResponse<ExerciseQuizResponse> getAllExercises(
            int page, int size,
            SortField sortBy, boolean asc) {

        Pageable pageable = PageRequest.of(
                page - 1,
                size,
                SortHelper.build(sortBy, asc));

        Page<ExerciseQuizResponse> pageData = exerciseRepository
                .findAll(pageable)
                .map(exerciseMapper::toExerciseQuizResponseFromExercise);

        return PageResponseHelper.toPageResponse(pageData, page);
    }

    public PageResponse<ExerciseQuizResponse> getExercisesOf(
            int page, int size,
            SortField sortBy, boolean asc) {

        Pageable pageable = PageRequest.of(
                page - 1,
                size,
                SortHelper.build(sortBy, asc));

        Page<ExerciseQuizResponse> pageData = exerciseRepository
                .findByUserId(AuthenticationHelper.getMyUserId(), pageable)
                .map(exerciseMapper::toExerciseQuizResponseFromExercise);

        return PageResponseHelper.toPageResponse(pageData, page);
    }

    public ExerciseQuizDetailResponse getExerciseDetail(
            String exerciseId,
            int qPage, int qSize,
            SortField qSortBy, boolean qAsc) {

        Exercise exercise =
                exerciseHelper.getExerciseOrThrow(exerciseId);

        QuizDetailSliceDetailResponse qSlice =
                quizHelper.buildQuizSliceWithOptions(
                        exercise, qPage, qSize, qSortBy, qAsc
                );

        return ExerciseQuizDetailResponse.builder()
                .id(exercise.getId())
                .userId(exercise.getUserId())
                .title(exercise.getTitle())
                .description(exercise.getDescription())
                .exerciseType(exercise.getExerciseType())
                .difficulty(exercise.getDifficulty())
                .orgId(exercise.getOrgId())
                .active(exercise.isActive())
                .cost(exercise.getCost())
                .freeForOrg(exercise.isFreeForOrg())
                .startTime(exercise.getStartTime())
                .endTime(exercise.getEndTime())
                .duration(exercise.getDuration())
                .allowDiscussionId(exercise.getAllowDiscussionId())
                .resourceIds(exercise.getResourceIds())
                .tags(exercise.getTags())
                .allowAiQuestion(exercise.isAllowAiQuestion())
                .quizDetail(qSlice)
                .visibility(exercise.isVisibility())
                .createdBy(exercise.getCreatedBy())
                .createdAt(exercise.getCreatedAt())
                .updatedBy(exercise.getUpdatedBy())
                .updatedAt(exercise.getUpdatedAt())
                .deletedBy(exercise.getDeletedBy())
                .deletedAt(exercise.getDeletedAt())
                .build();
    }
}
