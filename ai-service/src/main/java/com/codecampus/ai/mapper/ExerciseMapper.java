package com.codecampus.ai.mapper;

import com.codecampus.ai.dto.request.exercise.OptionDto;
import com.codecampus.ai.dto.request.exercise.QuestionDto;
import com.codecampus.ai.dto.request.exercise.QuestionGenDto;
import org.mapstruct.Mapper;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

    default QuestionDto mapQuestionGenToQuestionDto(
            QuestionGenDto questionGenDto) {
        return new QuestionDto(
                questionGenDto.text(), questionGenDto.questionType(),
                questionGenDto.points(), questionGenDto.orderInQuiz(),
                questionGenDto.options().stream()
                        .map(optionGenDto -> new OptionDto(
                                optionGenDto.optionText(),
                                optionGenDto.correct(),
                                optionGenDto.order()))
                        .collect(Collectors.toList()));
    }
}
