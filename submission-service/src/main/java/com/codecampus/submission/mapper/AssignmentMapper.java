package com.codecampus.submission.mapper;

import com.codecampus.quiz.grpc.AssignmentDto;
import com.codecampus.submission.entity.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {

    @Mapping(target = "exerciseId", source = "exercise.id")
    AssignmentDto toGrpc(Assignment assignment);
}
