package com.codecampus.submission.mapper;

import com.codecampus.submission.dto.response.contest.ContestResponse;
import com.codecampus.submission.entity.Contest;
import com.codecampus.submission.mapper.filter.RoleFilterMapper;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {ExerciseMapper.class}
)
public interface ContestMapper
    extends RoleFilterMapper<Contest, ContestResponse> {
  ContestResponse toContestResponse(Contest contest);
}
