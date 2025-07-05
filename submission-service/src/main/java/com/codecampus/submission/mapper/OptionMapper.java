package com.codecampus.submission.mapper;

import com.codecampus.submission.dto.request.OptionDto;
import com.codecampus.submission.entity.Option;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OptionMapper
{
  @Mapping(target = "question", ignore = true)
  Option toOption(OptionDto dto);
}
