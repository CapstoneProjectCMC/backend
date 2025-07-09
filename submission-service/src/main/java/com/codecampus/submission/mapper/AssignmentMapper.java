package com.codecampus.submission.mapper;

import com.codecampus.quiz.grpc.AssignmentDto;
import com.codecampus.submission.entity.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {

    @Mapping(target = "exerciseId", source = "exercise.id")
    AssignmentDto toGrpc(Assignment assignment);

    default com.google.protobuf.Timestamp map(java.time.Instant instant) {
        return instant == null ? null
                : com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    default java.time.Instant map(com.google.protobuf.Timestamp ts) {
        return ts == null ? null
                : java.time.Instant.ofEpochSecond(ts.getSeconds(),
                ts.getNanos());
    }
}
