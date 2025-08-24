package com.codecampus.submission.mapper;

import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.constant.submission.SubmissionStatus;
import com.codecampus.submission.dto.response.AllSubmissionHistoryResponse;
import com.codecampus.submission.dto.response.coding.CodingAttemptHistoryResponse;
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
        double ratio = (totalPoints > 0) ? (score * 1.0 / totalPoints) : 0.0;

        SubmissionStatus status;
        if (score == 0) {
            status = SubmissionStatus.FAILED;
        } else if (ratio >= 0.85) {
            status = SubmissionStatus.PASSED; // >=85% là pass
        } else {
            status = SubmissionStatus.PARTIAL;
        }
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

        Integer totalPoints = (submission.getExercise() != null
                && submission.getExercise().getQuizDetail() != null)
                ? submission.getExercise().getQuizDetail().getTotalPoints()
                : null;

        Integer score = submission.getScore();
        boolean pass = totalPoints != null && totalPoints > 0
                && score != null
                && (score * 100) >= (85 * totalPoints); // ≥85%

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
                submission.getSubmittedAt(),
                pass
        );
    }

    default CodingAttemptHistoryResponse mapSubmissionToCodingAttemptHistoryResponse(
            Submission submission) {
        Integer totalPoints = null;
        if (submission.getExercise() != null &&
                submission.getExercise().getCodingDetail() != null) {
            totalPoints =
                    submission.getExercise().getCodingDetail().getTestCases()
                            .size();
        }

        Integer score = submission.getScore(); // #testcase passed
        boolean pass = score != null
                && score.equals(totalPoints); // pass hết testcase

        return new CodingAttemptHistoryResponse(
                submission.getId(),
                submission.getExercise().getId(),
                submission.getExercise().getTitle(),
                submission.getScore(), // #passed
                totalPoints, // #testcases
                submission.getTimeTakenSeconds(),
                submission.getLanguage(),
                submission.getMemoryUsed(), // peakMemoryMb được set khi sync
                submission.getStatus(),
                submission.getSubmittedAt(),
                pass
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

        Integer score = submission.getScore();
        boolean passed;
        if (exercise.getExerciseType() == ExerciseType.QUIZ) {
            passed = totalPoints != null && totalPoints > 0
                    && score != null
                    && (score * 100) >= (85 * totalPoints); // ≥85%
        } else { // CODING
            passed = score != null
                    && score.equals(totalPoints);           // pass hết testcase
        }

        return new AllSubmissionHistoryResponse(
                submission.getId(),
                exercise.getId(),
                exercise.getTitle(),
                exercise.getExerciseType(),
                submission.getScore(),
                totalPoints,
                submission.getTimeTakenSeconds(),
                submission.getSubmittedAt(),
                passed
        );
    }
}
