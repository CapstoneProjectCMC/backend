package com.codecampus.submission.mapper;

import com.codecampus.submission.dto.request.CreateExerciseRequest;
import com.codecampus.submission.dto.request.UpdateExerciseRequest;
import com.codecampus.submission.entity.Exercise;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ExerciseMapper {
    @Mapping(target = "userId", expression = "java(userId)")
    @Mapping(target = "visibility", expression =
            "java(request.orgId()==null || request.orgId().isBlank())")
    Exercise toExercise(
            CreateExerciseRequest request,
            @Context String userId);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patch(
            UpdateExerciseRequest request,
            @MappingTarget Exercise exercise
    );
}
