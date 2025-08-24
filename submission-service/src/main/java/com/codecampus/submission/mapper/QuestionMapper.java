package com.codecampus.submission.mapper;

import com.codecampus.quiz.grpc.OptionDto;
import com.codecampus.quiz.grpc.QuestionDto;
import com.codecampus.quiz.grpc.QuestionType;
import com.codecampus.submission.dto.request.quiz.UpdateQuestionRequest;
import com.codecampus.submission.entity.Option;
import com.codecampus.submission.entity.Question;
import java.util.Comparator;
import java.util.Optional;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface QuestionMapper {

  private static QuestionType mapQuestionTypeGrpcToQuestionType(
      com.codecampus.submission.constant.submission.QuestionType t) {
    return switch (t) {
      case SINGLE_CHOICE -> QuestionType.SINGLE_CHOICE;
      case MULTI_CHOICE -> QuestionType.MULTI_CHOICE;
      case FILL_BLANK -> QuestionType.FILL_BLANK;
    };
  }

  @Mapping(target = "quizDetail", ignore = true)
  Question toQuestionFromQuestionDto(
      com.codecampus.submission.dto.request.quiz.QuestionDto dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchUpdateQuestionRequestToQuestion(
      UpdateQuestionRequest request,
      @MappingTarget Question question
  );

  @AfterMapping
  default void linkOptionsToQuestion(@MappingTarget Question question) {
    if (question.getOptions() != null) {
      question.getOptions()
          .forEach(option -> option.setQuestion(question));
    }
  }

  default QuestionDto toQuestionDtoFromQuestion(Question question) {
    QuestionDto.Builder builder = QuestionDto.newBuilder()
        .setId(question.getId())
        .setText(question.getText())
        .setQuestionType(
            mapQuestionTypeGrpcToQuestionType(
                question.getQuestionType()))
        .setPoints(question.getPoints())
        .setOrderInQuiz(question.getOrderInQuiz());

    if (question.getOptions() != null) {
      question.getOptions().stream()
          .filter(option -> !option.isDeleted())
          .sorted(Comparator.comparing(Option::getOrder))
          .forEach(option -> builder
              .addOptions(toOptionDtoFromOption(option)));
    }
    return builder.build();
  }

  default OptionDto toOptionDtoFromOption(Option option) {
    return OptionDto.newBuilder()
        .setId(option.getId() == null ? "" : option.getId())
        .setOptionText(
            Optional.ofNullable(option.getOptionText()).orElse(""))
        .setOrder(
            Optional.ofNullable(option.getOrder()).orElse(""))
        .setCorrect(option.isCorrect())
        .build();
  }
}
