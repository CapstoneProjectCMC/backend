package com.codecampus.submission.mapper.coding;

import com.codecampus.submission.dto.data.TestCaseData;
import com.codecampus.submission.entity.TestCase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TestCaseMapper
{
  @Mapping(target = "exercise", ignore = true)
  TestCase toTestCase(TestCaseData testCaseData);
}
