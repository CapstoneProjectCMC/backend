package com.codecampus.quiz.service.grpc;

import com.codecampus.quiz.grpc.LoadQuizRequest;
import com.codecampus.quiz.grpc.LoadQuizResponse;
import com.codecampus.quiz.grpc.QuizPlayServiceGrpc;
import com.codecampus.quiz.grpc.SubmitQuizRequest;
import com.codecampus.quiz.grpc.SubmitQuizResponse;
import com.codecampus.quiz.service.QuizService;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizPlayServiceImpl
        extends QuizPlayServiceGrpc.QuizPlayServiceImplBase {

    QuizService quizService;

    @Override
    @Transactional
    public void loadQuiz(
            LoadQuizRequest loadQuizRequest,
            StreamObserver<LoadQuizResponse> responseObserver) {
        LoadQuizResponse response = quizService.loadQuiz(
                loadQuizRequest.getExerciseId(),
                loadQuizRequest.getStudentId()
        );

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void submitQuiz(
            SubmitQuizRequest submitQuizRequest,
            StreamObserver<SubmitQuizResponse> responseObserver) {

        SubmitQuizResponse response = quizService.submitQuiz(submitQuizRequest);

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
