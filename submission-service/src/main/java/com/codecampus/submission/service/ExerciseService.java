package com.codecampus.submission.service;

import com.codecampus.submission.constant.sort.SortField;
import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.dto.common.PageResponse;
import com.codecampus.submission.dto.request.CreateExerciseRequest;
import com.codecampus.submission.dto.request.UpdateExerciseRequest;
import com.codecampus.submission.dto.response.quiz.ExerciseDetailQuizResponse;
import com.codecampus.submission.dto.response.quiz.ExerciseQuizResponse;
import com.codecampus.submission.dto.response.quiz.QuizDetailSliceDto;
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
import com.codecampus.submission.repository.ExerciseRepository;
import com.codecampus.submission.repository.QuestionRepository;
import com.codecampus.submission.repository.SubmissionRepository;
import com.codecampus.submission.service.grpc.GrpcCodingClient;
import com.codecampus.submission.service.grpc.GrpcQuizClient;
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

    GrpcQuizClient grpcQuizClient;
    GrpcCodingClient grpcCodingClient;

    ExerciseMapper exerciseMapper;
    SubmissionMapper submissionMapper;

    QuizHelper quizHelper;
    ExerciseHelper exerciseHelper;

    @Transactional
    public void createExercise(
            CreateExerciseRequest request) {
        Exercise exercise = exerciseRepository
                .save(exerciseMapper.toExerciseFromCreateExerciseRequest(
                        request, AuthenticationHelper.getMyUserId()));
        if (exercise.getExerciseType() == ExerciseType.QUIZ) {
            grpcQuizClient.pushExercise(exercise);
        } else if (exercise.getExerciseType() == ExerciseType.CODING) {
            grpcCodingClient.pushExercise(exercise);
        }
    }


    @Transactional
    public void createQuizSubmission(
            CreateQuizSubmissionRequest request) {
        QuizSubmissionDto dto = request.getSubmission();

        Exercise exercise = exerciseHelper
                .getExerciseOrThrow(dto.getExerciseId());

        Submission submission =
                submissionMapper.toSubmissionFromQuizSubmissionDto(
                        dto, exercise, questionRepository);
        submissionRepository.save(submission);
    }

    @Transactional
    public void updateExercise(
            String id, UpdateExerciseRequest request) {
        Exercise exercise = exerciseHelper
                .getExerciseOrThrow(id);
        exerciseMapper.patchUpdateExerciseRequestToExercise(request, exercise);

        grpcQuizClient.pushExercise(exercise);
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

    public ExerciseDetailQuizResponse getExerciseDetail(
            String exerciseId,
            int qPage, int qSize,
            SortField qSortBy, boolean qAsc) {

        Exercise exercise =
                exerciseHelper.getExerciseOrThrow(exerciseId);

        QuizDetailSliceDto qSlice = quizHelper.buildQuizSlice(
                exercise, qPage, qSize, qSortBy, qAsc
        );

        ExerciseDetailQuizResponse exerciseDetailQuizResponse =
                exerciseMapper.toExerciseDetailQuizResponseFromExercise(
                        exercise);

        return new ExerciseDetailQuizResponse(
                exerciseDetailQuizResponse.id(),
                exerciseDetailQuizResponse.userId(),
                exerciseDetailQuizResponse.title(),
                exerciseDetailQuizResponse.description(),
                exerciseDetailQuizResponse.exerciseType(),
                exerciseDetailQuizResponse.difficulty(),
                exerciseDetailQuizResponse.orgId(),
                exerciseDetailQuizResponse.active(),
                exerciseDetailQuizResponse.cost(),
                exerciseDetailQuizResponse.freeForOrg(),
                exerciseDetailQuizResponse.startTime(),
                exerciseDetailQuizResponse.endTime(),
                exerciseDetailQuizResponse.duration(),
                exerciseDetailQuizResponse.allowDiscussionId(),
                exerciseDetailQuizResponse.resourceIds(),
                exerciseDetailQuizResponse.tags(),
                qSlice,
                exerciseDetailQuizResponse.createdBy(),
                exerciseDetailQuizResponse.createdAt(),
                exerciseDetailQuizResponse.updatedBy(),
                exerciseDetailQuizResponse.updatedAt(),
                exerciseDetailQuizResponse.deletedBy(),
                exerciseDetailQuizResponse.deletedAt()
        );
    }
}
