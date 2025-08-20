package com.codecampus.search.mapper;

import com.codecampus.search.dto.response.ExerciseSearchResponse;
import com.codecampus.search.entity.ExerciseDocument;
import events.exercise.data.ExercisePayload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

    ExerciseDocument toExerciseDocumentFromExercisePayload(
            ExercisePayload exercisePayload);

    ExercisePayload toExercisePayloadFromExerciseDocument(
            ExerciseDocument exerciseDocument);

    ExerciseSearchResponse toExerciseSearchResponseFromExerciseDocument(
            ExerciseDocument exerciseDocument);
}
