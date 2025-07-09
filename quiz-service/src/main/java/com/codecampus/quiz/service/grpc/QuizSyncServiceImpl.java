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
            StreamObserver<Empty> streamObserver) {

        quizService.createQuizExercise(createQuizRequest);

        streamObserver.onNext(Empty.getDefaultInstance());
        streamObserver.onCompleted();
    }

    @Override
    @Transactional
    public void addQuizDetail(
            AddQuizDetailRequest addQuizRequest,
            StreamObserver<Empty> streamObserver) {

        quizService.addQuizDetail(addQuizRequest);

        streamObserver.onNext(Empty.getDefaultInstance());
        streamObserver.onCompleted();
    }

    @Override
    @Transactional
    public void addQuestion(
            AddQuestionRequest addQuestionRequest,
            StreamObserver<Empty> streamObserver) {

        quizService.addQuestion(addQuestionRequest);

        streamObserver.onNext(Empty.getDefaultInstance());
        streamObserver.onCompleted();
    }

    @Override
    @Transactional
    public void addOption(
            AddOptionRequest req,
            StreamObserver<Empty> obs) {
        
        quizService.addOption(req);

        obs.onNext(Empty.getDefaultInstance());
        obs.onCompleted();
    }

    @Override
    @Transactional
    public void upsertAssignment(
            UpsertAssignmentRequest request,
            StreamObserver<Empty> responseObserver) {

        quizService.upsertAssignment(request);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}

