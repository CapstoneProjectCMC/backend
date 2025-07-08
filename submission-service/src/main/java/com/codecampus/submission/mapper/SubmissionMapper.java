package com.codecampus.submission.mapper;

import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Question;
import com.codecampus.submission.entity.Submission;
import com.codecampus.submission.entity.SubmissionAnswer;
import com.codecampus.submission.entity.data.SubmissionAnswerId;
import com.codecampus.submission.grpc.QuizSubmissionDto;
import com.codecampus.submission.repository.QuestionRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {

    @Mapping(target = "exercise", ignore = true)
    @Mapping(target = "submittedAt",
            expression = "java(java.time.Instant.ofEpochSecond(" +
                    "dto.getSubmittedAt().getSeconds(), " +
                    "dto.getSubmittedAt().getNanos()))")
    Submission toEntity(
            QuizSubmissionDto dto,
            @Context Exercise exercise,
            @Context QuestionRepository questionRepository);

    @AfterMapping
    default void link(@MappingTarget Submission sub,
                      QuizSubmissionDto dto,
                      @Context Exercise exercise,
                      @Context QuestionRepository questionRepo) {

        dto.getAnswersList().forEach(a -> {
            Question q = questionRepo.findById(a.getQuestionId())
                    .orElseThrow();
            SubmissionAnswer ans = new SubmissionAnswer(
                    new SubmissionAnswerId(sub.getId(), q.getId()),
                    sub, q,
                    q.getOptions().stream()
                            .filter(o -> o.getId()
                                    .equals(a.getSelectedOptionId()))
                            .findFirst()
                            .orElse(null),
                    a.getAnswerText(),
                    a.getCorrect());
            sub.getAnswers().add(ans);
        });
    }

    @AfterMapping
    default void afterMapping(
            @MappingTarget Submission sub,
            QuizSubmissionDto dto,
            @Context Exercise exercise,
            @Context QuestionRepository questionRepo) {

        sub.setExercise(exercise);

        dto.getAnswersList().forEach(a -> {
            Question q = questionRepo.findById(a.getQuestionId()).orElseThrow();
            SubmissionAnswer ans = new SubmissionAnswer(
                    new SubmissionAnswerId(sub.getId(), q.getId()),
                    sub, q,
                    q.getOptions().stream()
                            .filter(o -> o.getId()
                                    .equals(a.getSelectedOptionId()))
                            .findFirst().orElse(null),
                    a.getAnswerText(),
                    a.getCorrect());
            sub.getAnswers().add(ans);
        });
    }
}
