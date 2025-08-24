package com.codecampus.quiz.mapper;

import com.codecampus.quiz.entity.QuizSubmission;
import com.codecampus.quiz.entity.QuizSubmissionAnswer;
import com.codecampus.quiz.grpc.AnswerDto;
import com.codecampus.submission.grpc.QuizSubmissionAnswerDto;
import com.codecampus.submission.grpc.QuizSubmissionDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {

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

  default QuizSubmissionAnswerDto toQuizSubmissionAnswerDtoFromQuizSubmissionAnswer(
      QuizSubmissionAnswer answer) {
    QuizSubmissionAnswerDto.Builder builder =
        QuizSubmissionAnswerDto.newBuilder()
            .setQuestionId(answer.getQuestion().getId())
            .setCorrect(answer.isCorrect());

    // SINGLE / MULTI choice
    if (answer.getSelectedOption() != null) {
      builder.setSelectedOptionId(answer.getSelectedOption().getId());
    }

    // FILL_BLANK
    if (answer.getAnswerText() != null) {
      builder.setAnswerText(answer.getAnswerText());
    }

    return builder.build();
  }
}
