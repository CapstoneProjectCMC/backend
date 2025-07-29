package com.codecampus.submission.mapper;

import com.codecampus.submission.entity.Exercise;
import events.exercise.data.ExercisePayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExercisePayloadMapper {
    @Mapping(target = "exerciseType", expression = "java(e.getExerciseType().name())")
    ExercisePayload toExercisePayloadFromExercise(Exercise e);
}
