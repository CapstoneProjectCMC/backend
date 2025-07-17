package com.codecampus.search.mapper;

import com.codecampus.search.entity.ExerciseDocument;
import event.data.ExercisePayload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

    ExerciseDocument toExerciseDocumentFromExercisePayload(
            ExercisePayload exercisePayload);

    ExercisePayload toExercisePayloadFromExerciseDocument(
            ExerciseDocument exerciseDocument);
}
