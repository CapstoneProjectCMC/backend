package com.codecampus.submission.mapper;

import com.codecampus.submission.dto.request.contest.CreateContestRequest;
import com.codecampus.submission.entity.Contest;
import com.codecampus.submission.entity.Exercise;
import events.contest.data.ContestPayload;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring"
)
public interface ContestMapper {

  @Mapping(target = "exercises", ignore = true)
  @Mapping(target = "orgId", ignore = true)
  Contest toContestFromCreateContestRequest(
      CreateContestRequest request);

  default Set<String> toExerciseIds(Contest contest) {
    return contest.getExercises() == null ? Set.of()
        : contest.getExercises().stream()
        .map(Exercise::getId)
        .collect(Collectors.toSet());
  }

  default ContestPayload toContestPayloadFromContest(
      Contest contest) {
    return ContestPayload.builder()
        .id(contest.getId())
        .title(contest.getTitle())
        .startTime(contest.getStartTime())
        .endTime(contest.getEndTime())
        .rankPublic(contest.isRankPublic())
        .orgId(contest.getOrgId())
        .exerciseIds(contest.getExercises() == null ? null :
            contest.getExercises().stream().map(e -> e.getId())
                .collect(Collectors.toSet()))
        .build();
  }
}
