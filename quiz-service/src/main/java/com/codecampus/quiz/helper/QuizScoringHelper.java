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
                .timeTakenSeconds(request.getTimeTakenSeconds())
                .build();

        for (AnswerDto answerDto : request.getAnswersList()) {
            Question question = quizExercise
                    .findQuestionById(answerDto.getQuestionId())
                    .orElseThrow(
                            () -> new AppException(ErrorCode.QUESTION_NOT_FOUND)
                    );

            boolean correct = switch (question.getQuestionType()) {
                case SINGLE_CHOICE -> checkSingleChoice(question, answerDto);
                case MULTI_CHOICE -> checkMultiChoice(question, answerDto);
                case FILL_BLANK -> checkFillBlank(question, answerDto);
            };

            if (correct) {
                score += question.getPoints();
            }

            QuizSubmissionAnswer detail =
                    buildSubmissionAnswer(
                            submission,
                            question,
                            answerDto,
                            correct
                    );
            submission.getAnswers().add(detail);
        }

        submission.setScore(score);
        return submission;
    }

    public static boolean checkSingleChoice(
            Question question, AnswerDto answerDto) {
        return question.getOptions().stream()
                .filter(Option::isCorrect)
                .map(Option::getId)
                .anyMatch(correctId -> correctId.equals(
                        answerDto.getSelectedOptionId()));
    }

    public static boolean checkMultiChoice(
            Question question, AnswerDto answerDto) {
        Set<String> correctIds = question.getOptions().stream()
                .filter(Option::isCorrect)
                .map(Option::getId)
                .collect(toSet());

        Set<String> selectedIds = Arrays
                .stream(answerDto.getSelectedOptionId().split(","))
                .map(String::trim)
                .collect(toSet());

        return correctIds.equals(selectedIds);
    }

    public static boolean checkFillBlank(
            Question question,
            AnswerDto answerDto) {
        
        return question.getOptions().stream()
                .filter(Option::isCorrect)
                .flatMap(o -> Arrays.stream(o.getOptionText().split("\\|")))
                .map(String::trim)
                .anyMatch(correctAnswer ->
                        correctAnswer.equalsIgnoreCase(
                                answerDto.getAnswerText().trim()));
    }

    public static QuizSubmissionAnswer buildSubmissionAnswer(
            QuizSubmission submission,
            Question question,
            AnswerDto answerDto,
            boolean correct) {

        return QuizSubmissionAnswer.builder()
                .id(new QuizSubmissionAnswerId(submission.getId(),
                        question.getId()))
                .submission(submission)
                .question(question)
                .selectedOption(
                        question.findOptionById(answerDto.getSelectedOptionId())
                                .orElse(null))
                .answerText(answerDto.getAnswerText())
                .correct(correct)
                .build();
    }

    public static void recalc(QuizExercise quiz) {
        quiz.setNumQuestions(quiz.getQuestions().size());
        quiz.setTotalPoints(
                quiz.getQuestions().stream()
                        .mapToInt(Question::getPoints).sum());
    }
}
