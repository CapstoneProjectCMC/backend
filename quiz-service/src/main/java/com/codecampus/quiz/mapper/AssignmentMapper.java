package com.codecampus.quiz.mapper;

import com.codecampus.quiz.entity.Assignment;
import com.codecampus.quiz.grpc.AssignmentDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {

    Assignment toEntity(AssignmentDto dto);

    @InheritInverseConfiguration(name = "toEntity")
    AssignmentDto toGrpc(Assignment ent);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patch(
            AssignmentDto assignmentDto,
            @MappingTarget Assignment assignment
    );

    default Instant map(com.google.protobuf.Timestamp ts) {
        return ts == null ? null
                : Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos());
    }

    default com.google.protobuf.Timestamp map(Instant i) {
        return i == null ? null
                : com.google.protobuf.Timestamp.newBuilder()
                .setSeconds(i.getEpochSecond())
                .setNanos(i.getNano())
                .build();
    }
}
