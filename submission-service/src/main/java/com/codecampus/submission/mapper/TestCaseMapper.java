package com.codecampus.submission.mapper;

import com.codecampus.submission.dto.request.coding.TestCaseDto;
import com.codecampus.submission.dto.request.coding.UpdateTestCaseRequest;
import com.codecampus.submission.entity.TestCase;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TestCaseMapper {
    @Mapping(target = "codingDetail", ignore = true)
    TestCase toTestCase(TestCaseDto testCaseDto);

    List<TestCase> toTestCaseList(List<TestCaseDto> testCaseDtos);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patch(@MappingTarget TestCase testCase,
               UpdateTestCaseRequest testCaseRequest);
}
