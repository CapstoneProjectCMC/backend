package com.codecampus.quiz.helper;

import com.codecampus.quiz.entity.Option;
import com.codecampus.quiz.entity.Question;
import com.codecampus.quiz.entity.QuizExercise;
import com.codecampus.quiz.entity.QuizSubmission;
import com.codecampus.quiz.entity.QuizSubmissionAnswer;
import com.codecampus.quiz.entity.data.QuizSubmissionAnswerId;
import com.codecampus.quiz.exception.AppException;
import com.codecampus.quiz.exception.ErrorCode;
import com.codecampus.quiz.grpc.AnswerDto;
import com.codecampus.quiz.grpc.SubmitQuizRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuizScoringHelper {

    public static QuizSubmission score(
            QuizExercise quizExercise,
            SubmitQuizRequest request) {
        int score = 0;
        QuizSubmission submission = QuizSubmission.builder()
                .exerciseId(quizExercise.getId())
                .studentId(request.getStudentId())
                .submittedAt(Instant.now())
                .totalPoints(quizExercise.getTotalPoints())
                .build();

        for (AnswerDto ans : request.getAnswersList()) {
            Question question = quizExercise
                    .findQuestion(ans.getQuestionId())
                    .orElseThrow(
                            () -> new AppException(ErrorCode.QUESTION_NOT_FOUND)
                    );

            boolean correct = switch (question.getQuestionType()) {
                case SINGLE_CHOICE -> question.getOptions().stream()
                        .filter(Option::isCorrect)
                        .anyMatch(option -> option.getId()
                                .equals(ans.getSelectedOptionId()));
                case FILL_BLANK -> question.getOptions().isEmpty() &&
                        question.getText()
                                .equalsIgnoreCase(ans.getAnswerText());
                case MULTI_CHOICE -> {
                    Set<String> correctIds = question.getOptions().stream()
                            .filter(Option::isCorrect).map(Option::getId)
                            .collect(toSet());
                    Set<String> chosen =
                            Arrays.stream(ans.getSelectedOptionId().split(","))
                                    .collect(toSet());
                    yield correctIds.equals(chosen);
                }
            };

            if (correct) {
                score += question.getPoints();
            }

            QuizSubmissionAnswer detail = QuizSubmissionAnswer.builder()
                    .id(new QuizSubmissionAnswerId(submission.getId(),
                            question.getId()))
                    .submission(submission)
                    .question(question)
                    .selectedOption(
                            question.optionById(ans.getSelectedOptionId())
                                    .orElse(null))
                    .answerText(ans.getAnswerText())
                    .correct(correct)
                    .build();

            submission.getAnswers().add(detail);
        }

        submission.setScore(score);
        return submission;
    }

    public static void recalc(QuizExercise quiz) {
        quiz.setNumQuestions(quiz.getQuestions().size());
        quiz.setTotalPoints(
                quiz.getQuestions().stream()
                        .mapToInt(Question::getPoints).sum());
    }
}
