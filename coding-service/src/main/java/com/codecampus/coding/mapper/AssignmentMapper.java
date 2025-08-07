package com.codecampus.coding.mapper;

import com.codecampus.coding.entity.Assignment;
import com.codecampus.coding.grpc.AssignmentDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {
    Assignment toAssignmentFromAssignmentDto(
            AssignmentDto assignmentDto);

    AssignmentDto toAssignmentDtoFromAssignment(
            Assignment assignment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchAssignmentDtoToAssignment(
            AssignmentDto assignmentDto,
            @MappingTarget Assignment assignment);

    default Instant mapTimestampToInstant(
            com.google.protobuf.Timestamp timestamp) {
        return timestamp == null ? null :
                Instant.ofEpochSecond(timestamp.getSeconds(),
                        timestamp.getNanos());
    }

    default com.google.protobuf.Timestamp mapInstantToTimeStamp(
            Instant instant) {
        return instant == null ? null :
                com.google.protobuf.Timestamp.newBuilder()
                        .setSeconds(instant.getEpochSecond())
                        .setNanos(instant.getNano()).build();
    }
}
