package com.codecampus.submission.mapper.quiz;

import com.codecampus.submission.dto.data.QuestionData;
import com.codecampus.submission.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionMapper
{
  @Mapping(target = "exercise", ignore = true)
  @Mapping(target = "options", ignore = true)
  Question toQuestion(QuestionData questionData);
}
