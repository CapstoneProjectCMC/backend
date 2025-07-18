package com.codecampus.quiz.mapper;

import com.codecampus.quiz.entity.QuizSubmission;
import com.codecampus.quiz.entity.QuizSubmissionAnswer;
import com.codecampus.quiz.grpc.AnswerDto;
import com.codecampus.submission.grpc.QuizSubmissionAnswerDto;
import com.codecampus.submission.grpc.QuizSubmissionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {

    @Mapping(target = "questionId", source = "question.id")
    @Mapping(target = "selectedOptionId", source = "selectedOption.id")
    QuizSubmissionAnswerDto toQuizSubmissionAnswerDtoFromQuizSubmissionAnswer(
            QuizSubmissionAnswer e);

    QuizSubmissionAnswer toQuizSubmissionAnswerFromAnswerDto(
            AnswerDto answerDto);


    default com.google.protobuf.Timestamp mapInstantToProtobufTimestamp(
            java.time.Instant instant) {
        return com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    default QuizSubmissionDto toQuizSubmissionDtoFromQuizSubmission(
            QuizSubmission quizSubmission) {
        QuizSubmissionDto.Builder builder = QuizSubmissionDto.newBuilder()
                .setId(quizSubmission.getId())
                .setExerciseId(quizSubmission.getExerciseId())
                .setStudentId(quizSubmission.getStudentId())
                .setScore(quizSubmission.getScore())
                .setTotalPoints(quizSubmission.getTotalPoints())
                .setSubmittedAt(
                        mapInstantToProtobufTimestamp(
                                quizSubmission.getSubmittedAt()))
                .setTimeTakenSeconds(quizSubmission.getTimeTakenSeconds());
        quizSubmission.getAnswers()
                .forEach(a -> builder.addAnswers(
                        toQuizSubmissionAnswerDtoFromQuizSubmissionAnswer(a)));
        return builder.build();
    }
}
