package com.codecampus.submission.mapper;

import com.codecampus.submission.dto.request.quiz.OptionDto;
import com.codecampus.submission.dto.request.quiz.UpdateOptionRequest;
import com.codecampus.submission.entity.Option;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OptionMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patch(
            UpdateOptionRequest request,
            @MappingTarget Option option
    );

    @Mapping(target = "question", ignore = true)
    Option toOption(OptionDto dto);
}
