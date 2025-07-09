package com.codecampus.submission.service;

import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.dto.request.quiz.AddQuizDetailRequest;
import com.codecampus.submission.dto.request.quiz.OptionDto;
import com.codecampus.submission.dto.request.quiz.QuestionDto;
import com.codecampus.submission.dto.request.quiz.UpdateOptionRequest;
import com.codecampus.submission.dto.request.quiz.UpdateQuestionRequest;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Option;
import com.codecampus.submission.entity.Question;
import com.codecampus.submission.entity.QuizDetail;
import com.codecampus.submission.exception.AppException;
import com.codecampus.submission.exception.ErrorCode;
import com.codecampus.submission.helper.QuizHelper;
import com.codecampus.submission.mapper.OptionMapper;
import com.codecampus.submission.mapper.QuestionMapper;
import com.codecampus.submission.repository.ExerciseRepository;
import com.codecampus.submission.repository.OptionRepository;
import com.codecampus.submission.repository.QuestionRepository;
import com.codecampus.submission.repository.QuizDetailRepository;
import com.codecampus.submission.service.grpc.GrpcQuizClient;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizService {
    QuizDetailRepository quizDetailRepository;
    QuestionRepository questionRepository;
    OptionRepository optionRepository;
    ExerciseRepository exerciseRepository;
    GrpcQuizClient grpcQuizClient;

    QuestionMapper questionMapper;
    OptionMapper optionMapper;

    @Transactional
    public QuizDetail addQuizDetail(
            String exerciseId,
            AddQuizDetailRequest addQuizDetailRequest) {
        Exercise exercise = getExerciseOrThrow(exerciseId);

        Assert.isTrue(
                exercise.getExerciseType() == ExerciseType.QUIZ,
                "Exercise không phải QUIZ"
        );
        if (exercise.getQuizDetail() != null) {
            throw new AppException(ErrorCode.EXERCISE_TYPE);
        }

        QuizDetail quizDetail = new QuizDetail();
        quizDetail.setExercise(exercise);

        int total = 0;
        for (QuestionDto questionDto : addQuizDetailRequest.questions()) {
            Question question =
                    questionMapper.toQuestion(questionDto);
            question.setQuizDetail(quizDetail);
            quizDetail.getQuestions().add(question);
            total += question.getPoints();
        }
        quizDetail.setNumQuestions(quizDetail.getQuestions().size());
        quizDetail.setTotalPoints(total);
        quizDetailRepository.save(quizDetail);

        grpcQuizClient.pushQuizDetail(exerciseId, quizDetail);
        return quizDetail;
    }

    @Transactional
    public Question addQuestion(
            String exerciseId,
            QuestionDto questionDto) throws BadRequestException {

        Exercise exercise = getExerciseOrThrow(exerciseId);
        QuizDetail quizDetail = Optional
                .ofNullable(exercise.getQuizDetail())
                .orElseThrow(
                        () -> new BadRequestException("Chưa có QuizDetail")
                );

        Question question =
                questionMapper.toQuestion(questionDto);
        question.setQuizDetail(quizDetail);

        quizDetail.getQuestions().add(question);
        quizDetail.setNumQuestions(quizDetail.getNumQuestions() + 1);
        quizDetail.setTotalPoints(
                quizDetail.getTotalPoints() + question.getPoints());
        questionRepository.save(question);

        grpcQuizClient.pushQuestion(exerciseId, question);
        return question;
    }

    @Transactional
    public Option addOption(
            String questionId,
            OptionDto optionDto) {
        Question question = getQuestionOrThrow(questionId);

        Option option = optionMapper.toOption(optionDto);
        option.setQuestion(question);
        question.getOptions().add(option);
        optionRepository.save(option);

        grpcQuizClient.pushOption(
                question.getQuizDetail().getExercise().getId(),
                questionId,
                option);

        return option;
    }

    @Transactional
    public Question updateQuestion(
            String exerciseId,
            String questionId,
            UpdateQuestionRequest request) {
        Exercise exercise = getExerciseOrThrow(exerciseId);

        Question question = exercise.getQuizDetail()
                .getQuestions()
                .stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(
                        () -> new AppException(ErrorCode.QUESTION_NOT_FOUND)
                );

        questionMapper.patch(request, question);
        QuizHelper.recalcQuiz(exercise.getQuizDetail());

        grpcQuizClient.pushQuestion(exerciseId, question); // sync
        return question;
    }

    @Transactional
    public Option updateOption(
            String optionId,
            UpdateOptionRequest request) {
        Option option = optionRepository
                .findById(optionId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.OPTION_NOT_FOUND)
                );
        optionMapper.patch(request, option);

        grpcQuizClient.pushQuestion(
                option.getQuestion().getQuizDetail().getExercise().getId(),
                option.getQuestion());
        return option;
    }

    Exercise getExerciseOrThrow(String exerciseId) {
        return exerciseRepository.findById(exerciseId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.EXERCISE_NOT_FOUND));
    }

    Question getQuestionOrThrow(String questionId) {
        return questionRepository
                .findById(questionId)
                .orElseThrow(
                        () -> new AppException(ErrorCode.QUESTION_NOT_FOUND));
    }
}
