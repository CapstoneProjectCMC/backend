package com.codecampus.submission.helper;

import com.codecampus.submission.entity.Exercise;
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
}
