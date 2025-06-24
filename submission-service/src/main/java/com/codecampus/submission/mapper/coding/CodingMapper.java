package com.codecampus.submission.mapper.coding;

import com.codecampus.submission.dto.data.CodingData;
import com.codecampus.submission.entity.CodingDetail;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    uses = {TestCaseMapper.class}
)
public interface CodingMapper
{
  @BeanMapping(ignoreUnmappedSourceProperties = {"testCases"})
  @Mapping(target = "exercise", ignore = true)
  CodingDetail toCodingDetail(CodingData codingData);
}
