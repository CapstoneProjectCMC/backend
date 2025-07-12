package com.codecampus.submission.service;

import com.codecampus.submission.constant.sort.SortField;
import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.dto.common.PageResponse;
import com.codecampus.submission.dto.request.CreateExerciseRequest;
import com.codecampus.submission.dto.request.UpdateExerciseRequest;
import com.codecampus.submission.dto.response.quiz.ExerciseDetailQuizDto;
import com.codecampus.submission.dto.response.quiz.ExerciseQuizDto;
import com.codecampus.submission.dto.response.quiz.QuizDetailSliceDto;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Submission;
import com.codecampus.submission.exception.AppException;
import com.codecampus.submission.exception.ErrorCode;
import com.codecampus.submission.grpc.CreateQuizSubmissionRequest;
import com.codecampus.submission.grpc.QuizSubmissionDto;
import com.codecampus.submission.helper.AuthenticationHelper;
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
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Transactional
    public void createExercise(
            CreateExerciseRequest request) {
        Exercise exercise = exerciseRepository
                .save(exerciseMapper.toExercise(
                        request, AuthenticationHelper.getMyUserId()));
        if (exercise.getExerciseType() == ExerciseType.QUIZ) {
            grpcQuizClient.pushExercise(exercise);
        } else if (exercise.getExerciseType() == ExerciseType.CODING) {
            grpcCodingClient.pushExercise(exercise);
        }
    }

//    LỖI không biết fix lỗi đồng bộ 2 GRPC -> KHÔNG TÌM THẤY BÀI TẬP
//    @Transactional
//    public Exercise createQuizExerciseWithQuiz(
//            CreateQuizExerciseRequest request) {
//        Exercise exercise = exerciseRepository
//                .save(exerciseMapper.toExercise(
//                        request.exercise(),
//                        AuthenticationHelper.getMyUserId()));
//
//        assertType(exercise, ExerciseType.QUIZ);
//
//        String exerciseId = exercise.getId();
//
//        QuizDetail quizDetail =
//                quizService.addQuizDetail(exerciseId, request.quiz());
//
//        // gRPC sync
//        grpcQuizClient.pushExercise(exercise);
//        grpcQuizClient.pushQuizDetail(exerciseId, quizDetail);
//
//        return exercise;
//    }
//
//    @Transactional
//    public Exercise createExerciseWithCoding(
//            CreateCodingExerciseRequest request) {
//        Exercise exercise = exerciseRepository.save(
//                exerciseMapper.toExercise(
//                        request.exercise(),
//                        AuthenticationHelper.getMyUserId()));
//
//        assertType(exercise, ExerciseType.CODING);
//
//        String exerciseId = exercise.getId();
//
//        CodingDetail codingDetail =
//                codingService.addCodingDetail(exerciseId, request.coding());
//        return exercise;
//    }

    @Transactional
    public void createQuizSubmission(
            CreateQuizSubmissionRequest request) {
        QuizSubmissionDto dto = request.getSubmission();

        // 1. lấy Exercise, Question sẵn có
        Exercise exercise = exerciseRepository
                .findById(dto.getExerciseId())
                .orElseThrow(
                        () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
                );

        Submission submission = submissionMapper.toEntity(
                dto, exercise, questionRepository);
        submissionRepository.save(submission);
    }

    @Transactional
    public void updateExercise(
            String id, UpdateExerciseRequest request) {
        Exercise exercise = getExerciseOrThrow(id);
        exerciseMapper.patch(request, exercise);

        grpcQuizClient.pushExercise(exercise);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<ExerciseQuizDto> getAllExercises(
            int exPage, int exSize,
            SortField exSortBy, boolean exAsc,
            int qPage, Integer qSize,
            SortField qSortBy, boolean qAsc) {

        Pageable exPageable = PageRequest.of(
                exPage - 1,
                exSize,
                SortHelper.build(exSortBy, exAsc));

        Page<Exercise> exPageData = exerciseRepository
                .findAll(exPageable);

        return quizHelper.buildPageResponseExerciseQuizDto(
                exPage,
                qPage, qSize,
                qSortBy, qAsc,
                exPageData);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public PageResponse<ExerciseQuizDto> getExercisesOf(
            int exPage, int exSize,
            SortField exSortBy, boolean exAsc,
            int qPage, int qSize,
            SortField qSortBy, boolean qAsc) {

        Pageable exPageable = PageRequest.of(
                exPage - 1,
                exSize,
                SortHelper.build(exSortBy, exAsc));

        Page<Exercise> exPageData = exerciseRepository
                .findByUserId(AuthenticationHelper.getMyUserId(), exPageable);

        return quizHelper.buildPageResponseExerciseQuizDto(
                exPage,
                qPage, qSize,
                qSortBy, qAsc, exPageData);
    }

    public ExerciseDetailQuizDto getExerciseDetail(
            String exerciseId,
            int qPage, int qSize,
            SortField qSortBy, boolean qAsc) {

        Exercise exercise = getExerciseOrThrow(exerciseId);

        QuizDetailSliceDto qSlice = quizHelper.buildQuizSlice(
                exercise, qPage, qSize, qSortBy, qAsc
        );

        ExerciseDetailQuizDto exerciseDetailQuizDto = exerciseMapper
                .toExerciseDetailQuizDto(exercise);

        return new ExerciseDetailQuizDto(
                exerciseDetailQuizDto.id(),
                exerciseDetailQuizDto.userId(),
                exerciseDetailQuizDto.title(),
                exerciseDetailQuizDto.description(),
                exerciseDetailQuizDto.exerciseType(),
                exerciseDetailQuizDto.orgId(),
                exerciseDetailQuizDto.active(),
                exerciseDetailQuizDto.cost(),
                exerciseDetailQuizDto.freeForOrg(),
                exerciseDetailQuizDto.startTime(),
                exerciseDetailQuizDto.endTime(),
                exerciseDetailQuizDto.duration(),
                exerciseDetailQuizDto.allowDiscussionId(),
                exerciseDetailQuizDto.resourceIds(),
                exerciseDetailQuizDto.tags(),
                qSlice,
                exerciseDetailQuizDto.createdBy(),
                exerciseDetailQuizDto.createdAt(),
                exerciseDetailQuizDto.updatedBy(),
                exerciseDetailQuizDto.updatedAt(),
                exerciseDetailQuizDto.deletedBy(),
                exerciseDetailQuizDto.deletedAt()
        );
    }

    void assertType(Exercise exercise, ExerciseType type) {
        if (exercise.getExerciseType() != type) {
            throw new AppException(ErrorCode.EXERCISE_TYPE);
        }
    }

    public Exercise getExerciseOrThrow(
            String exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
                );
    }
}
