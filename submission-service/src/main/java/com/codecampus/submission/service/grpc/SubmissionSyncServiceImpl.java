package com.codecampus.submission.service.grpc;

import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Submission;
import com.codecampus.submission.exception.AppException;
import com.codecampus.submission.exception.ErrorCode;
import com.codecampus.submission.grpc.CreateQuizSubmissionRequest;
import com.codecampus.submission.grpc.QuizSubmissionDto;
import com.codecampus.submission.grpc.SubmissionSyncServiceGrpc;
import com.codecampus.submission.mapper.SubmissionMapper;
import com.codecampus.submission.repository.ExerciseRepository;
import com.codecampus.submission.repository.QuestionRepository;
import com.codecampus.submission.repository.SubmissionAnswerRepository;
import com.codecampus.submission.repository.SubmissionRepository;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

@GrpcService
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SubmissionSyncServiceImpl
        extends SubmissionSyncServiceGrpc.SubmissionSyncServiceImplBase {

    ExerciseRepository exerciseRepository;
    QuestionRepository questionRepository;
    SubmissionRepository submissionRepository;
    SubmissionAnswerRepository answerRepository;
    SubmissionMapper submissionMapper;

    @Override
    @Transactional
    public void createQuizSubmission(
            CreateQuizSubmissionRequest request,
            StreamObserver<Empty> responseObserver) {
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

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
