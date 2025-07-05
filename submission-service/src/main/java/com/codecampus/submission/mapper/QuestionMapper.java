package com.codecampus.submission.mapper;

import com.codecampus.quiz.grpc.OptionDto;
import com.codecampus.quiz.grpc.QuestionDto;
import com.codecampus.quiz.grpc.QuestionType;
import com.codecampus.submission.entity.Option;
import com.codecampus.submission.entity.Question;
import java.util.Comparator;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface QuestionMapper
{

  /* helper */
  private static QuestionType mapType(com.codecampus.submission.constant.submission.QuestionType t)
  {
    return switch (t)
    {
      case SINGLE_CHOICE -> QuestionType.SINGLE_CHOICE;
      case MULTI_CHOICE -> QuestionType.MULTI_CHOICE;
      case FILL_BLANK -> QuestionType.FILL_BLANK;
    };
  }

  /* ===== DTO → Entity (đã có) ===== */
  @Mapping(target = "quizDetail", ignore = true)
  Question toQuestion(com.codecampus.submission.dto.request.QuestionDto dto);

  @AfterMapping
  default void link(@MappingTarget Question q)
  {
    if (q.getOptions() != null)
    {
      q.getOptions().forEach(o -> o.setQuestion(q));
    }
  }

  /* ===== Entity → gRPC (mới thêm) ===== */
  default QuestionDto toGrpc(Question q)
  {
    QuestionDto.Builder b = QuestionDto.newBuilder()
        .setId(q.getId())
        .setText(q.getText())
        .setQuestionType(mapType(q.getQuestionType()))
        .setPoints(q.getPoints())
        .setOrderInQuiz(q.getOrderInQuiz());

    if (q.getOptions() != null)
    {
      q.getOptions().stream()
          .sorted(Comparator.comparing(Option::getOrder))
          .forEach(o -> b.addOptions(toGrpc(o)));
    }
    return b.build();
  }

  default OptionDto toGrpc(Option o)
  {
    return OptionDto.newBuilder()
        .setId(o.getId())
        .setOptionText(o.getOptionText())
        .setOrder(o.getOrder())
        .build();
  }
}
