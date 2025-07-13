package com.codecampus.submission.mapper;

import com.codecampus.submission.dto.request.CreateExerciseRequest;
import com.codecampus.submission.dto.request.UpdateExerciseRequest;
import com.codecampus.submission.dto.response.quiz.ExerciseDetailQuizResponse;
import com.codecampus.submission.dto.response.quiz.ExerciseQuizResponse;
import com.codecampus.submission.dto.response.quiz.QuestionBriefDto;
import com.codecampus.submission.dto.response.quiz.QuizDetailSliceDto;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Question;
import com.codecampus.submission.entity.QuizDetail;
import com.codecampus.submission.repository.QuestionRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ExerciseMapper {

    @Mapping(target = "userId", expression = "java(userId)")
    @Mapping(target = "visibility", expression =
            "java(request.orgId()==null || request.orgId().isBlank())")
    Exercise toExerciseFromCreateExerciseRequest(
            CreateExerciseRequest request,
            @Context String userId);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patchUpdateExerciseRequestToExercise(
            UpdateExerciseRequest request,
            @MappingTarget Exercise exercise
    );

    ExerciseQuizResponse toExerciseQuizResponseFromExercise(Exercise e);

    @Mapping(target = "quizDetail", ignore = true)
    ExerciseDetailQuizResponse toExerciseDetailQuizResponseFromExercise(
            Exercise e);

    @Mapping(target = "type",
            expression = "java(question.getQuestionType().name())")
    QuestionBriefDto toQuestionBriefDtoFromQuestion(Question question);

    default QuizDetailSliceDto toQuizDetailSliceDtoFromQuizDetail(
            QuizDetail quizDetail,
            Page<Question> questionPage,
            @Context QuestionRepository questionRepository) {

        List<QuestionBriefDto> questionBriefDtos = questionPage
                .getContent()
                .stream()
                .map(this::toQuestionBriefDtoFromQuestion)
                .toList();

        return new QuizDetailSliceDto(
                quizDetail.getId(),
                quizDetail.getNumQuestions(),
                quizDetail.getTotalPoints(),

                questionPage.getNumber() + 1,
                questionPage.getTotalPages(),
                questionPage.getSize(),
                questionPage.getTotalElements(),

                questionBriefDtos,

                quizDetail.getCreatedBy(),
                quizDetail.getCreatedAt(),
                quizDetail.getUpdatedBy(),
                quizDetail.getUpdatedAt(),
                quizDetail.getDeletedBy(),
                quizDetail.getDeletedAt()
        );
    }

    @AfterMapping
    default void attachQuizDetailExerciseDetailQuizDto(
            @MappingTarget
            ExerciseDetailQuizResponse.ExerciseDetailQuizResponseBuilder builder,
            Exercise exercise,
            @Context QuizDetailSliceDto quizDetail) {
        builder.quizDetail(quizDetail);
    }
}
