package com.codecampus.submission.service;

import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.dto.request.CreateExerciseRequest;
import com.codecampus.submission.dto.request.UpdateExerciseRequest;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Submission;
import com.codecampus.submission.exception.AppException;
import com.codecampus.submission.exception.ErrorCode;
import com.codecampus.submission.grpc.CreateQuizSubmissionRequest;
import com.codecampus.submission.grpc.QuizSubmissionDto;
import com.codecampus.submission.helper.AuthenticationHelper;
import com.codecampus.submission.mapper.ExerciseMapper;
import com.codecampus.submission.mapper.QuestionMapper;
import com.codecampus.submission.mapper.SubmissionMapper;
import com.codecampus.submission.repository.ExerciseRepository;
import com.codecampus.submission.repository.QuestionRepository;
import com.codecampus.submission.repository.QuizDetailRepository;
import com.codecampus.submission.repository.SubmissionRepository;
import com.codecampus.submission.service.grpc.GrpcQuizClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseService {
    ExerciseRepository exerciseRepository;
    QuizDetailRepository quizDetailRepository;
    QuestionRepository questionRepository;
    SubmissionRepository submissionRepository;

    GrpcQuizClient grpcQuizClient;

    ExerciseMapper exerciseMapper;
    SubmissionMapper submissionMapper;
    QuestionMapper questionMapper;

    QuizService quizService;
    CodingService codingService;

    @Transactional
    public Exercise createExercise(
            CreateExerciseRequest request) {
        Exercise exercise = exerciseRepository
                .save(exerciseMapper.toExercise(
                        request, AuthenticationHelper.getMyUserId()));
        grpcQuizClient.pushExercise(exercise);
        return exercise;
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
    public Exercise updateExercise(
            String id, UpdateExerciseRequest request) {
        Exercise exercise = getExerciseOrThrow(id);
        exerciseMapper.patch(request, exercise);

        grpcQuizClient.pushExercise(exercise);
        return exercise; // JPA dirty-checking
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public List<Exercise> getExercisesOf() {
        return exerciseRepository.findByUserId(
                AuthenticationHelper.getMyUserId());
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
