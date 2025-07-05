package com.codecampus.quiz.mapper;

import com.codecampus.quiz.entity.Option;
import com.codecampus.quiz.entity.Question;
import com.codecampus.quiz.entity.QuizExercise;
import com.codecampus.quiz.grpc.LoadQuizResponse;
import com.codecampus.quiz.grpc.OptionDto;
import com.codecampus.quiz.grpc.QuestionDto;
import com.codecampus.quiz.grpc.QuestionType;
import com.codecampus.quiz.grpc.QuizExerciseDto;
import java.util.Comparator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QuizMapper
{
  /* ===== gRPC  →  Entity (đồng bộ) =============== */

  private static QuestionType mapType(
      com.codecampus.quiz.constant.submission.QuestionType questionType)
  {
    return switch (questionType)
    {
      case SINGLE_CHOICE -> QuestionType.SINGLE_CHOICE;
      case MULTI_CHOICE -> QuestionType.MULTI_CHOICE;
      case FILL_BLANK -> QuestionType.FILL_BLANK;
    };
  }

  @Mapping(target = "questions", ignore = true)
  QuizExercise toEntity(QuizExerciseDto dto);

  @Mapping(target = "quiz", ignore = true)
  @Mapping(target = "questionType",
      expression = "java(asEntityEnum(dto.getQuestionType()))")
  Question toEntity(QuestionDto dto);

  /* ===== Entity → gRPC (học sinh) ================ */

  @Mapping(target = "question", ignore = true)
  Option toEntity(OptionDto dto);

  /* ===== helpers ===== */

  /**
   * Trả về LoadQuizResponse và **không** đưa cờ correct ra ngoài.
   */
  default LoadQuizResponse toLoadQuizResponse(QuizExercise quiz)
  {

    // Header exercise
    QuizExerciseDto exerciseDto = QuizExerciseDto.newBuilder()
        .setId(quiz.getId())
        .setTitle(quiz.getTitle())
        .setDescription(
            quiz.getDescription() == null ? "" : quiz.getDescription())
        .setTotalPoints(quiz.getTotalPoints())
        .setNumQuestions(quiz.getNumQuestions())
        .build();

    LoadQuizResponse.Builder resp = LoadQuizResponse.newBuilder()
        .setExercise(exerciseDto);

    // Questions (ẩn 'correct')
    quiz.getQuestions().stream()
        .sorted(Comparator.comparingInt(Question::getOrderInQuiz))
        .forEach(q -> resp.addQuestions(toDtoHideCorrect(q)));

    return resp.build();
  }

  default QuestionDto toDtoHideCorrect(Question q)
  {
    QuestionDto.Builder b = QuestionDto.newBuilder()
        .setId(q.getId())
        .setText(q.getText())
        .setQuestionType(mapType(q.getQuestionType()))
        .setPoints(q.getPoints())
        .setOrderInQuiz(q.getOrderInQuiz());

    q.getOptions().stream()
        .sorted(Comparator.comparing(Option::getOrder))
        .forEach(o -> b.addOptions(toDtoHideCorrect(o)));

    return b.build();
  }

  default OptionDto toDtoHideCorrect(Option o)
  {
    // **Không** set field 'correct'
    return OptionDto.newBuilder()
        .setId(o.getId())
        .setOptionText(o.getOptionText())
        .setOrder(o.getOrder())
        .build();
  }

  /* ----------------- Enum mapping ------------------ */

  /**
   * Map GRPC → Entity; UNRECOGNIZED gán mặc định SINGLE_CHOICE
   */
  default com.codecampus.quiz.constant.submission.QuestionType asEntityEnum(QuestionType t)
  {
    return switch (t)
    {
      case MULTI_CHOICE -> com.codecampus.quiz.constant.submission.QuestionType.MULTI_CHOICE;
      case FILL_BLANK -> com.codecampus.quiz.constant.submission.QuestionType.FILL_BLANK;
      case SINGLE_CHOICE, UNRECOGNIZED ->
          com.codecampus.quiz.constant.submission.QuestionType.SINGLE_CHOICE;
    };
  }

  /**
   * Map Entity → GRPC (không có UNRECOGNIZED)
   */
  default QuestionType asGrpcEnum(com.codecampus.quiz.constant.submission.QuestionType t)
  {
    return switch (t)
    {
      case MULTI_CHOICE -> QuestionType.MULTI_CHOICE;
      case FILL_BLANK -> QuestionType.FILL_BLANK;
      case SINGLE_CHOICE -> QuestionType.SINGLE_CHOICE;
    };
  }
}