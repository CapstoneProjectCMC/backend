package com.codecampus.submission.helper;

import com.codecampus.submission.entity.CodingDetail;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.QuizDetail;
import com.codecampus.submission.exception.AppException;
import com.codecampus.submission.exception.ErrorCode;
import com.codecampus.submission.repository.ExerciseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ExerciseHelper {

    ExerciseRepository exerciseRepository;

    public Exercise getExerciseOrThrow(
            String exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
                );
    }

    public void markExerciseDeletedRecursively(
            Exercise exercise,
            String by) {
        exercise.markDeleted(by);

        if (exercise.getCodingDetail() != null) {
            CodingDetail codingDetail = exercise.getCodingDetail();
            codingDetail.markDeleted(by);
            codingDetail.getTestCases()
                    .forEach(tc -> tc.markDeleted(by));
        }
        if (exercise.getQuizDetail() != null) {
            QuizDetail quizDetail = exercise.getQuizDetail();
            quizDetail.markDeleted(by);
            quizDetail.getQuestions()
                    .forEach(question -> {
                        question.markDeleted(by);
                        question.getOptions()
                                .forEach(option -> option.markDeleted(by));
                    });
        }
    }
}
