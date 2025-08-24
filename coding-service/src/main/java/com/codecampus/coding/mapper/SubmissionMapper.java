package com.codecampus.coding.mapper;

import com.codecampus.coding.entity.CodeSubmission;
import com.codecampus.submission.grpc.CodeSubmissionDto;
import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {
  @Mapping(target = "exerciseId", source = "exercise.id")
  @Mapping(target = "submittedAt", source = "submittedAt",
      qualifiedByName = "instantToTimestamp")
  @Mapping(target = "resultsList", ignore = true)
  @Mapping(target = "score", ignore = true)
  @Mapping(target = "totalPoints", ignore = true)
  CodeSubmissionDto toCodeSubmissionDtoFromCodeSubmission(
      CodeSubmission codeSubmission);

  /* ------- Convert Instant → protobuf.Timestamp ------- */
  @Named("instantToTimestamp")
  default Timestamp mapInstantToTimestamp(Instant instant) {
    return instant == null ? null :
        Timestamp.newBuilder()
            .setSeconds(instant.getEpochSecond())
            .setNanos(instant.getNano())
            .build();
  }


  default List<CodeSubmissionDto> toCodeSubmissionDtoListFromCodeSubmissionList(
      List<CodeSubmission> codeSubmissionList) {
    return codeSubmissionList
        .stream()
        .map(this::toCodeSubmissionDtoFromCodeSubmission)
        .toList();
  }
}
