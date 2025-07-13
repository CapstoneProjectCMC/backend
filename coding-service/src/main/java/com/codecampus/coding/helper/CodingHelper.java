package com.codecampus.coding.helper;

import com.codecampus.coding.entity.CodingExercise;
import com.codecampus.coding.exception.AppException;
import com.codecampus.coding.exception.ErrorCode;
import com.codecampus.coding.repository.CodingExerciseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CodingHelper {

    CodingExerciseRepository codingExerciseRepository;

    public CodingExercise findCodingOrThrow(String exerciseId) {
        return codingExerciseRepository
                .findById(exerciseId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND)
                );
    }
}
