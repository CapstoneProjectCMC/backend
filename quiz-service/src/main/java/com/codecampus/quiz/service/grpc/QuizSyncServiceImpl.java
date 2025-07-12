package com.codecampus.quiz.service.grpc;

import com.codecampus.quiz.grpc.AddOptionRequest;
import com.codecampus.quiz.grpc.AddQuestionRequest;
import com.codecampus.quiz.grpc.AddQuizDetailRequest;
import com.codecampus.quiz.grpc.CreateQuizExerciseRequest;
import com.codecampus.quiz.grpc.QuizSyncServiceGrpc;
import com.codecampus.quiz.grpc.UpsertAssignmentRequest;
import com.codecampus.quiz.service.QuizService;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizSyncServiceImpl
        extends QuizSyncServiceGrpc.QuizSyncServiceImplBase {

    QuizService quizService;

    @Override
    @Transactional
    public void createQuizExercise(
            CreateQuizExerciseRequest createQuizRequest,
            StreamObserver<Empty> responseObserver) {

        quizService.createQuizExercise(createQuizRequest);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void addQuizDetail(
            AddQuizDetailRequest addQuizRequest,
            StreamObserver<Empty> responseObserver) {

        quizService.addQuizDetail(addQuizRequest);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void addQuestion(
            AddQuestionRequest addQuestionRequest,
            StreamObserver<Empty> responseObserver) {

        quizService.addQuestion(addQuestionRequest);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void addOption(
            AddOptionRequest addOptionRequest,
            StreamObserver<Empty> responseObserver) {

        quizService.addOption(addOptionRequest);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void upsertAssignment(
            UpsertAssignmentRequest upsertAssignmentRequest,
            StreamObserver<Empty> responseObserver) {

        quizService.upsertAssignment(upsertAssignmentRequest);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}

