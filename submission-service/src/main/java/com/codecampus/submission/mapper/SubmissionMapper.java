package com.codecampus.submission.mapper;

import com.codecampus.submission.constant.submission.SubmissionStatus;
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

    @Mapping(target = "id", ignore = true)             // QUAN TRỌNG
    @Mapping(target = "exercise", ignore = true)
    @Mapping(target = "userId", source = "studentId")
    @Mapping(target = "submittedAt",
            expression = "java(java.time.Instant.ofEpochSecond(" +
                    "dto.getSubmittedAt().getSeconds(), " +
                    "dto.getSubmittedAt().getNanos()))")
    Submission toSubmissionFromQuizSubmissionDto(
            QuizSubmissionDto dto,
            @Context Exercise exercise,
            @Context QuestionRepository questionRepository);

    @AfterMapping
    default void linkQuizSubmissionDtoToSubmission(
            @MappingTarget Submission submission,
            QuizSubmissionDto quizSubmissionDto,
            @Context Exercise exercise,
            @Context QuestionRepository questionRepository) {

        submission.setExercise(exercise);

        /* ---- GÁN STATUS ---- */
        int score = quizSubmissionDto.getScore();
        int totalPoints = quizSubmissionDto.getTotalPoints();

        SubmissionStatus status = switch (score) {
            case 0 -> SubmissionStatus.FAILED;
            default -> (score >= totalPoints)
                    ? SubmissionStatus.PASSED
                    : SubmissionStatus.PARTIAL;
        };
        submission.setStatus(status);

        quizSubmissionDto.getAnswersList().forEach(answerDto -> {
            Question question =
                    questionRepository.findById(answerDto.getQuestionId())
                            .orElseThrow();
            SubmissionAnswer ans = new SubmissionAnswer(
                    new SubmissionAnswerId(submission.getId(),
                            question.getId()),
                    submission, question,
                    question.getOptions().stream()
                            .filter(option -> option.getId()
                                    .equals(answerDto.getSelectedOptionId()))
                            .findFirst()
                            .orElse(null),
                    answerDto.getAnswerText(),
                    answerDto.getCorrect());
            submission.getAnswers().add(ans);
        });
    }
}
