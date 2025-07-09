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

    /* ---------- Load quiz cho học sinh ---------- */
    @Override
    @Transactional
    public void loadQuiz(
            LoadQuizRequest loadQuizRequest,
            StreamObserver<LoadQuizResponse> responseObserver) {
        responseObserver.onNext(
                quizService.loadQuiz(
                        loadQuizRequest.getExerciseId(),
                        loadQuizRequest.getStudentId()
                )
        );
        responseObserver.onCompleted();
    }

    /* ---------- Submit quiz ---------- */
    @Override
    @Transactional
    public void submitQuiz(
            SubmitQuizRequest submitQuizRequest,
            StreamObserver<SubmitQuizResponse> responseObserver) {

        responseObserver.onNext(quizService.submitQuiz(submitQuizRequest));
        responseObserver.onCompleted();
    }
}
