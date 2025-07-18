package com.codecampus.submission.mapper;

import com.codecampus.submission.dto.request.contest.CreateContestRequest;
import com.codecampus.submission.entity.Contest;
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
}
