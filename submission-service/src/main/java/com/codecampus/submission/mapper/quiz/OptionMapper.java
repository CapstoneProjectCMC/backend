package com.codecampus.submission.mapper.quiz;

import com.codecampus.submission.dto.data.OptionData;
import com.codecampus.submission.entity.Option;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OptionMapper
{
  @Mapping(target = "question", ignore = true)
  Option toOption(OptionData optionData);
}
