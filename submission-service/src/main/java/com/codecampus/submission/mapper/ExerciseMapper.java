package com.codecampus.submission.mapper;

import com.codecampus.submission.dto.request.AddCodingDetailRequest;
import com.codecampus.submission.dto.request.OptionDto;
import com.codecampus.submission.dto.request.QuestionDto;
import com.codecampus.submission.dto.request.TestCaseDto;
import com.codecampus.submission.entity.CodingDetail;
import com.codecampus.submission.entity.Option;
import com.codecampus.submission.entity.Question;
import com.codecampus.submission.entity.TestCase;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExerciseMapper
{
  // CODE
  CodingDetail toCodingDetail(
      AddCodingDetailRequest request);

  List<TestCase> toTestCases(
      List<TestCaseDto> dtos);

  // QUIZ
  List<Question> toQuestions(
      List<QuestionDto> dtos);

  List<Option> toOptions(
      List<OptionDto> dtos);
}
