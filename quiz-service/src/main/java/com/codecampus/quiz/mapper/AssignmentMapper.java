package com.codecampus.quiz.mapper;

import com.codecampus.quiz.entity.Assignment;
import com.codecampus.quiz.grpc.AssignmentDto;
import java.time.Instant;
import org.mapstruct.BeanMapping;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {

  Assignment toAssignmentFromAssignmentDto(AssignmentDto assignmentDto);

  @InheritInverseConfiguration(name = "toAssignmentFromAssignmentDto")
  AssignmentDto toAssignmentDtoFromAssignment(Assignment assignment);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchAssignmentDtoToAssignment(
      AssignmentDto assignmentDto,
      @MappingTarget Assignment assignment
  );

  default Instant mapProtobufTimestampToInstant(
      com.google.protobuf.Timestamp timestamp) {
    return timestamp == null ? null
        : Instant.ofEpochSecond(timestamp.getSeconds(),
        timestamp.getNanos());
  }

  default com.google.protobuf.Timestamp mapInstantToProtobufTimestamp(
      Instant instant) {
    return instant == null ? null
        : com.google.protobuf.Timestamp.newBuilder()
        .setSeconds(instant.getEpochSecond())
        .setNanos(instant.getNano())
        .build();
  }
}
