package com.codecampus.quiz.mapper;

import com.codecampus.quiz.entity.Option;
import com.codecampus.quiz.entity.Question;
import com.codecampus.quiz.entity.QuizExercise;
import com.codecampus.quiz.grpc.LoadQuizResponse;
import com.codecampus.quiz.grpc.OptionDto;
import com.codecampus.quiz.grpc.OptionDtoLoadResponse;
import com.codecampus.quiz.grpc.QuestionDto;
import com.codecampus.quiz.grpc.QuestionDtoLoadResponse;
import com.codecampus.quiz.grpc.QuestionType;
import com.codecampus.quiz.grpc.QuizExerciseDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.Comparator;
import java.util.Optional;

import static java.util.stream.Collectors.toSet;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QuizMapper {

    private static QuestionType mapQuestionTypeToQuestionTypeGrpc(
            com.codecampus.quiz.constant.submission.QuestionType questionType) {
        return switch (questionType) {
            case SINGLE_CHOICE -> QuestionType.SINGLE_CHOICE;
            case MULTI_CHOICE -> QuestionType.MULTI_CHOICE;
            case FILL_BLANK -> QuestionType.FILL_BLANK;
        };
    }

    @Mapping(target = "quiz", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchQuestionDtoToQuestion(
            QuestionDto questionDto,
            @MappingTarget Question question
    );

    @Mapping(target = "question", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchOptionDtoToOption(
            OptionDto optionDto,
            @MappingTarget Option option
    );

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchQuizExerciseDtoToQuizExercise(
            QuizExerciseDto quizExerciseDto,
            @MappingTarget QuizExercise quizExercise
    );

    @Mapping(target = "questions", ignore = true)
    QuizExercise toQuizExerciseFromQuizExerciseDto(
            QuizExerciseDto quizExerciseDto);

    @Mapping(target = "quiz", ignore = true)
    @Mapping(target = "questionType",
            expression = "java(mapEntityEnumQuestionType(questionDto.getQuestionType()))")
    Question toQuestionFromQuestionDto(QuestionDto questionDto);


    @Mapping(target = "question", ignore = true)
    Option toOptionFromOptionDto(OptionDto optionDto);

    default LoadQuizResponse toLoadQuizResponseFromQuizExercise(
            QuizExercise quiz) {

        return LoadQuizResponse.newBuilder()
                .setExercise(toQuizExerciseDtoFromQuizExercise(quiz))
                .addAllQuestions(quiz.getQuestions().stream()
                        .map(this::toQuestionDtoLoadResponseFromQuestionHideCorrect)
                        .collect(toSet()))
                .build();
    }

    default QuestionDtoLoadResponse toQuestionDtoLoadResponseFromQuestionHideCorrect(
            Question question) {
        QuestionDtoLoadResponse.Builder builder =
                QuestionDtoLoadResponse.newBuilder()
                        .setId(question.getId())
                        .setText(question.getText())
                        .setQuestionType(
                                mapQuestionTypeToQuestionTypeGrpc(
                                        question.getQuestionType()))
                        .setPoints(question.getPoints())
                        .setOrderInQuiz(question.getOrderInQuiz());

        question.getOptions().stream()
                .sorted(Comparator.comparing(Option::getOrder))
                .forEach(
                        option -> builder.addOptions(
                                toOptionDtoLoadResponseFromOptionHideCorrect(
                                        option)));

        return builder.build();
    }

    default OptionDtoLoadResponse toOptionDtoLoadResponseFromOptionHideCorrect(
            Option option) {
        // Không set field 'correct'
        return OptionDtoLoadResponse.newBuilder()
                .setId(option.getId())
                .setOptionText(option.getOptionText())
                .setOrder(option.getOrder())
                .build();
    }

    default com.codecampus.quiz.constant.submission.QuestionType mapEntityEnumQuestionType(
            QuestionType t) {
        return switch (t) {
            case MULTI_CHOICE ->
                    com.codecampus.quiz.constant.submission.QuestionType.MULTI_CHOICE;
            case FILL_BLANK ->
                    com.codecampus.quiz.constant.submission.QuestionType.FILL_BLANK;
            case SINGLE_CHOICE, UNRECOGNIZED ->
                    com.codecampus.quiz.constant.submission.QuestionType.SINGLE_CHOICE;
        };
    }

    default QuestionType mapGrpcEnumQuestionType(
            com.codecampus.quiz.constant.submission.QuestionType t) {
        return switch (t) {
            case MULTI_CHOICE -> QuestionType.MULTI_CHOICE;
            case FILL_BLANK -> QuestionType.FILL_BLANK;
            case SINGLE_CHOICE -> QuestionType.SINGLE_CHOICE;
        };
    }

    @AfterMapping
    default void linkOptionsToQuestion(@MappingTarget Question question) {
        if (question.getOptions() != null) {
            question.getOptions()
                    .forEach(option -> option.setQuestion(question));
        }
    }

    @Mapping(target = "questions", ignore = true)
    default QuizExerciseDto toQuizExerciseDtoFromQuizExercise(QuizExercise e) {
        return QuizExerciseDto.newBuilder()
                .setId(e.getId())
                .setTitle(e.getTitle())
                .setDescription(
                        Optional.ofNullable(e.getDescription()).orElse(""))
                .setTotalPoints(e.getTotalPoints())
                .setNumQuestions(e.getNumQuestions())
                .setDuration(e.getDuration())
                .build();
    }
}