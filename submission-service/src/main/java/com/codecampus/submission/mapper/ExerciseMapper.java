package com.codecampus.submission.mapper;

import com.codecampus.submission.dto.request.CreateExerciseRequest;
import com.codecampus.submission.dto.request.UpdateExerciseRequest;
import com.codecampus.submission.dto.response.quiz.ExerciseDetailQuizDto;
import com.codecampus.submission.dto.response.quiz.ExerciseQuizDto;
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
    Exercise toExercise(
            CreateExerciseRequest request,
            @Context String userId);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void patch(
            UpdateExerciseRequest request,
            @MappingTarget Exercise exercise
    );

    @Mapping(target = "type",
            expression = "java(question.getQuestionType().name())")
    QuestionBriefDto toQuestionBriefDto(Question question);

    default QuizDetailSliceDto toQuizDetailSliceDto(
            QuizDetail q,
            Page<Question> questionPage,
            @Context QuestionRepository repo) {

        List<QuestionBriefDto> qs = questionPage.getContent()
                .stream().map(this::toQuestionBriefDto).toList();

        return new QuizDetailSliceDto(
                q.getId(),
                q.getNumQuestions(),
                q.getTotalPoints(),

                questionPage.getNumber() + 1,
                questionPage.getTotalPages(),
                questionPage.getSize(),
                questionPage.getTotalElements(),

                qs,

                q.getCreatedBy(),
                q.getCreatedAt(),
                q.getUpdatedBy(),
                q.getUpdatedAt(),
                q.getDeletedBy(),
                q.getDeletedAt()
        );
    }

    @Mapping(target = "quizDetail", ignore = true)
    ExerciseQuizDto toExerciseQuizDto(Exercise e);

    @Mapping(target = "quizDetail", ignore = true)
    ExerciseDetailQuizDto toExerciseDetailQuizDto(Exercise e);

    @AfterMapping
    default void attachQuizDetailExerciseQuizDto(
            @MappingTarget ExerciseQuizDto.ExerciseQuizDtoBuilder b,
            Exercise e,
            @Context QuizDetailSliceDto quizDetail) {
        b.quizDetail(quizDetail);
    }

    @AfterMapping
    default void attachQuizDetailExerciseDetailQuizDto(
            @MappingTarget ExerciseDetailQuizDto.ExerciseDetailQuizDtoBuilder b,
            Exercise e,
            @Context QuizDetailSliceDto quizDetail) {
        b.quizDetail(quizDetail);
    }
}
