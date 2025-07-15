package com.codecampus.quiz.helper;

import com.codecampus.quiz.entity.Question;
import com.codecampus.quiz.entity.QuizExercise;
import com.codecampus.quiz.exception.AppException;
import com.codecampus.quiz.exception.ErrorCode;
import com.codecampus.quiz.repository.QuestionRepository;
import com.codecampus.quiz.repository.QuizExerciseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class QuizHelper {

    QuizExerciseRepository quizExerciseRepository;
    QuestionRepository questionRepository;

    public QuizExercise findQuizOrThrow(String exerciseId) {
        return quizExerciseRepository
                .findById(exerciseId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
                );
    }

    public Question findQuestionOrThrow(String questionId) {
        return questionRepository
                .findById(questionId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.QUESTION_NOT_FOUND)
                );
    }
}
