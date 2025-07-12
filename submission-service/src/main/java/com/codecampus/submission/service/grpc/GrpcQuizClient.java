package com.codecampus.submission.service.grpc;

import com.codecampus.quiz.grpc.AddOptionRequest;
import com.codecampus.quiz.grpc.AddQuestionRequest;
import com.codecampus.quiz.grpc.AddQuizDetailRequest;
import com.codecampus.quiz.grpc.CreateQuizExerciseRequest;
import com.codecampus.quiz.grpc.QuizSyncServiceGrpc;
import com.codecampus.quiz.grpc.UpsertAssignmentRequest;
import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.entity.Assignment;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Option;
import com.codecampus.submission.entity.Question;
import com.codecampus.submission.entity.QuizDetail;
import com.codecampus.submission.mapper.AssignmentMapper;
import com.codecampus.submission.mapper.QuestionMapper;
import com.codecampus.submission.mapper.QuizMapper;
import io.grpc.StatusRuntimeException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GrpcQuizClient {
    QuizSyncServiceGrpc.QuizSyncServiceBlockingStub stub;
    QuestionMapper questionMapper;
    QuizMapper quizMapper;
    AssignmentMapper assignmentMapper;

    @Transactional
    public void pushExercise(Exercise exercise) {
        if (exercise.getExerciseType() != ExerciseType.QUIZ) {
            return;
        }
        try {
            CreateQuizExerciseRequest createRequest =
                    CreateQuizExerciseRequest.newBuilder()
                            .setExercise(quizMapper.toGrpc(exercise))
                            .build();
            stub.createQuizExercise(createRequest);
        } catch (StatusRuntimeException ex) {
            log.error("[gRPC] pushExercise lỗi: {}", ex.getStatus(), ex);
            throw ex;
        }
    }

    @Transactional
    public void pushQuizDetail(
            String exerciseId,
            QuizDetail quizDetail) {
        AddQuizDetailRequest addQuizRequest =
                AddQuizDetailRequest.newBuilder()
                        .setExerciseId(exerciseId)
                        .addAllQuestions(
                                quizDetail.getQuestions()
                                        .stream()
                                        .map(questionMapper::toGrpc)
                                        .toList())
                        .build();
        stub.addQuizDetail(addQuizRequest);
    }

    @Transactional
    public void pushQuestion(
            String exerciseId,
            Question question) {
        AddQuestionRequest addQuestionRequest =
                AddQuestionRequest.newBuilder()
                        .setExerciseId(exerciseId)
                        .setQuestion(questionMapper.toGrpc(question))
                        .build();
        stub.addQuestion(addQuestionRequest);
    }

    @Transactional
    public void pushOption(
            String exerciseId,
            String questionId,
            Option option) {
        AddOptionRequest addOptionRequest = AddOptionRequest.newBuilder()
                .setExerciseId(exerciseId)
                .setQuestionId(questionId)
                .setOption(questionMapper.toGrpc(option))
                .build();

        stub.addOption(addOptionRequest);
    }

    @Transactional
    public void pushAssignment(Assignment assignment) {
        UpsertAssignmentRequest request = UpsertAssignmentRequest.newBuilder()
                .setAssignment(assignmentMapper.toGrpc(assignment))
                .build();

        stub.upsertAssignment(request);
    }
}
