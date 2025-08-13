package com.codecampus.submission.mapper;

import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.constant.submission.SubmissionStatus;
import com.codecampus.submission.dto.response.AllSubmissionHistoryResponse;
import com.codecampus.submission.dto.response.quiz.QuizAttemptHistoryResponse;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Question;
import com.codecampus.submission.entity.Submission;
import com.codecampus.submission.entity.SubmissionAnswer;
import com.codecampus.submission.entity.data.SubmissionAnswerId;
import com.codecampus.submission.grpc.QuizSubmissionDto;
import com.codecampus.submission.repository.QuestionRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercise", ignore = true)
    @Mapping(target = "userId", source = "studentId")
    @Mapping(target = "submittedAt",
            expression = "java(java.time.Instant.ofEpochSecond(" +
                    "dto.getSubmittedAt().getSeconds(), " +
                    "dto.getSubmittedAt().getNanos()))")
    Submission toSubmissionFromQuizSubmissionDto(
            QuizSubmissionDto dto,
            @Context Exercise exercise,
            @Context QuestionRepository questionRepository);

    @AfterMapping
    default void linkQuizSubmissionDtoToSubmission(
            @MappingTarget Submission submission,
            QuizSubmissionDto quizSubmissionDto,
            @Context Exercise exercise,
            @Context QuestionRepository questionRepository) {

        submission.setExercise(exercise);

        /* ---- GÁN STATUS ---- */
        int score = quizSubmissionDto.getScore();
        int totalPoints = quizSubmissionDto.getTotalPoints();

        SubmissionStatus status = switch (score) {
            case 0 -> SubmissionStatus.FAILED;
            default -> (score >= totalPoints)
                    ? SubmissionStatus.PASSED
                    : SubmissionStatus.PARTIAL;
        };
        submission.setStatus(status);

        quizSubmissionDto.getAnswersList().forEach(answerDto -> {
            Question question =
                    questionRepository.findById(answerDto.getQuestionId())
                            .orElseThrow();
            SubmissionAnswer ans = new SubmissionAnswer(
                    new SubmissionAnswerId(submission.getId(),
                            question.getId()),
                    submission, question,
                    question.getOptions().stream()
                            .filter(option -> option.getId()
                                    .equals(answerDto.getSelectedOptionId()))
                            .findFirst()
                            .orElse(null),
                    answerDto.getAnswerText(),
                    answerDto.getCorrect());
            submission.getAnswers().add(ans);
        });
    }

    default QuizAttemptHistoryResponse mapSubmissionToQuizAttemptHistoryResponse(
            Submission submission) {
        return new QuizAttemptHistoryResponse(
                submission.getId(),
                submission.getExercise().getId(),
                submission.getExercise().getTitle(),
                submission.getScore(),
                submission.getExercise().getQuizDetail() == null
                        ? null
                        : submission.getExercise().getQuizDetail()
                        .getTotalPoints(),
                submission.getTimeTakenSeconds(),
                submission.getSubmittedAt()
        );
    }

    default AllSubmissionHistoryResponse mapSubmissionToAllSubmissionHistoryResponse(
            Submission submission) {
        Exercise exercise = submission.getExercise();
        Integer totalPoints = null;

        if (exercise.getExerciseType() == ExerciseType.QUIZ &&
                exercise.getQuizDetail() != null) {
            totalPoints = exercise.getQuizDetail().getTotalPoints();
        } else if (exercise.getExerciseType() == ExerciseType.CODING &&
                exercise.getCodingDetail() != null) {
            totalPoints = exercise.getCodingDetail().getTestCases().size();
        }

        return new AllSubmissionHistoryResponse(
                submission.getId(),
                exercise.getId(),
                exercise.getTitle(),
                exercise.getExerciseType(),
                submission.getScore(),
                totalPoints,
                submission.getTimeTakenSeconds(),
                submission.getSubmittedAt()
        );
    }
}
