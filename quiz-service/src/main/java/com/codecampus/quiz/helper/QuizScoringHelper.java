package com.codecampus.quiz.helper;

import static java.util.stream.Collectors.toSet;

import com.codecampus.quiz.entity.Option;
import com.codecampus.quiz.entity.Question;
import com.codecampus.quiz.entity.QuizExercise;
import com.codecampus.quiz.entity.QuizSubmission;
import com.codecampus.quiz.entity.QuizSubmissionAnswer;
import com.codecampus.quiz.entity.data.QuizSubmissionAnswerId;
import com.codecampus.quiz.grpc.AnswerDto;
import com.codecampus.quiz.grpc.SubmitQuizRequest;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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

    Map<String, Question> activeQuestions =
        quizExercise.getQuestions().stream()
            .filter(q -> !q.isDeleted())
            .collect(Collectors.toMap(Question::getId, q -> q));

    for (AnswerDto answerDto : request.getAnswersList()) {
      Question question = activeQuestions.get(answerDto.getQuestionId());
      if (question == null) {
        // Câu hỏi đã bị xoá hoặc không còn hợp lệ -> bỏ qua
        continue;
      }

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
        .filter(o -> !o.isDeleted())
        .filter(Option::isCorrect)
        .map(Option::getId)
        .anyMatch(correctId -> correctId.equals(
            answerDto.getSelectedOptionId()));
  }

  public static boolean checkMultiChoice(
      Question question, AnswerDto answerDto) {

    Set<String> activeOptionIds = question.getOptions().stream()
        .filter(o -> !o.isDeleted())       // <---
        .map(Option::getId)
        .collect(toSet());

    Set<String> correctIds = question.getOptions().stream()
        .filter(o -> !o.isDeleted())
        .filter(Option::isCorrect)
        .map(Option::getId)
        .collect(toSet());

    Set<String> selectedIds = Arrays
        .stream(answerDto.getSelectedOptionId().split(","))
        .map(String::trim)
        .filter(activeOptionIds::contains) // bỏ id option đã xoá
        .collect(toSet());

    return correctIds.equals(selectedIds);
  }

  public static boolean checkFillBlank(
      Question question,
      AnswerDto answerDto) {

    return question.getOptions().stream()
        .filter(o -> !o.isDeleted())
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
    int num = (int) quiz.getQuestions().stream()
        .filter(q -> !q.isDeleted())
        .count();

    int total = quiz.getQuestions().stream()
        .filter(q -> !q.isDeleted())
        .mapToInt(Question::getPoints)
        .sum();

    quiz.setNumQuestions(num);
    quiz.setTotalPoints(total);
  }
}
