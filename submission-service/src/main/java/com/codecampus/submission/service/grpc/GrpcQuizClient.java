package com.codecampus.submission.service.grpc;

import com.codecampus.quiz.grpc.AddOptionRequest;
import com.codecampus.quiz.grpc.AddQuestionRequest;
import com.codecampus.quiz.grpc.AddQuizDetailRequest;
import com.codecampus.quiz.grpc.CreateQuizExerciseRequest;
import com.codecampus.quiz.grpc.QuizSyncServiceGrpc;
import com.codecampus.quiz.grpc.SoftDeleteOptionRequest;
import com.codecampus.quiz.grpc.SoftDeleteQuestionRequest;
import com.codecampus.quiz.grpc.SoftDeleteRequest;
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
                            .setExercise(
                                    quizMapper.toQuizExerciseDtoFromExercise(
                                            exercise))
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
                                        .map(questionMapper::toQuestionDtoFromQuestion)
                                        .toList())
                        .build();
        stub.addQuizDetail(addQuizRequest);
    }

    @Transactional
    public void softDeleteExercise(String exerciseId) {
        stub.softDeleteExercise(SoftDeleteRequest
                .newBuilder()
                .setId(exerciseId)
                .build()
        );
    }

    @Transactional
    public void pushQuestion(
            String exerciseId,
            Question question) {
        AddQuestionRequest addQuestionRequest =
                AddQuestionRequest.newBuilder()
                        .setExerciseId(exerciseId)
                        .setQuestion(questionMapper.toQuestionDtoFromQuestion(
                                question))
                        .build();
        stub.addQuestion(addQuestionRequest);
    }

    @Transactional
    public void softDeleteQuestion(
            String exerciseId,
            String questionId) {
        stub.softDeleteQuestion(SoftDeleteQuestionRequest
                .newBuilder()
                .setExerciseId(exerciseId)
                .setQuestionId(questionId)
                .build()
        );
    }

    @Transactional
    public void pushOption(
            String exerciseId,
            String questionId,
            Option option) {
        AddOptionRequest addOptionRequest = AddOptionRequest.newBuilder()
                .setExerciseId(exerciseId)
                .setQuestionId(questionId)
                .setOption(questionMapper.toOptionDtoFromOption(option))
                .build();

        stub.addOption(addOptionRequest);
    }

    @Transactional
    public void softDeleteOption(
            String exerciseId,
            String questionId,
            String optionId) {
        stub.softDeleteOption(SoftDeleteOptionRequest
                .newBuilder()
                .setExerciseId(exerciseId)
                .setQuestionId(questionId)
                .setOptionId(optionId)
                .build()
        );
    }

    @Transactional
    public void pushAssignment(Assignment assignment) {
        UpsertAssignmentRequest request = UpsertAssignmentRequest.newBuilder()
                .setAssignment(
                        assignmentMapper.toQuizAssignmentDtoFromAssignment(
                                assignment))
                .build();

        stub.upsertAssignment(request);
    }
}
