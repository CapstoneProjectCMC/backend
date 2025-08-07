package com.codecampus.submission.mapper;

import com.codecampus.quiz.grpc.AssignmentDto;
import com.codecampus.submission.entity.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {

    @Mapping(target = "exerciseId", source = "exercise.id")
    AssignmentDto toQuizAssignmentDtoFromAssignment(
            Assignment assignment);

    @Mapping(target = "exerciseId", source = "exercise.id")
    com.codecampus.coding.grpc.AssignmentDto toCodingAssignmentDtoFromAssignment(
            Assignment assignment);

    default com.google.protobuf.Timestamp mapInstantToProtobufTimestamp(
            java.time.Instant instant) {

        return instant == null ? null
                : com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    default java.time.Instant mapProtobufTimestampToInstant(
            com.google.protobuf.Timestamp timestamp) {

        return timestamp == null ? null
                : java.time.Instant.ofEpochSecond(
                timestamp.getSeconds(), timestamp.getNanos());
    }
}
