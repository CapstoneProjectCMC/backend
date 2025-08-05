package com.codecampus.coding.mapper;

import com.codecampus.coding.entity.CodeSubmission;
import com.codecampus.submission.grpc.CodeSubmissionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {
    @Mapping(target = "testCaseId", source = "id.testCaseId")
    CodeSubmissionDto toCodeSubmissionDtoFromCodeSubmission(
            CodeSubmission codeSubmission);

    default List<CodeSubmissionDto> toCodeSubmissionDtoListFromCodeSubmissionList(
            List<CodeSubmission> codeSubmissionList) {
        return codeSubmissionList
                .stream()
                .map(this::toCodeSubmissionDtoFromCodeSubmission)
                .toList();
    }
}
