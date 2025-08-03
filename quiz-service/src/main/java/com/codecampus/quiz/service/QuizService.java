package com.codecampus.quiz.service;

import com.codecampus.quiz.entity.Assignment;
import com.codecampus.quiz.entity.Option;
import com.codecampus.quiz.entity.Question;
import com.codecampus.quiz.entity.QuizExercise;
import com.codecampus.quiz.entity.QuizSubmission;
import com.codecampus.quiz.exception.AppException;
import com.codecampus.quiz.exception.ErrorCode;
import com.codecampus.quiz.grpc.AddOptionRequest;
import com.codecampus.quiz.grpc.AddQuestionRequest;
import com.codecampus.quiz.grpc.AddQuizDetailRequest;
import com.codecampus.quiz.grpc.AssignmentDto;
import com.codecampus.quiz.grpc.CreateQuizExerciseRequest;
import com.codecampus.quiz.grpc.LoadQuizResponse;
import com.codecampus.quiz.grpc.OptionDto;
import com.codecampus.quiz.grpc.QuestionDto;
import com.codecampus.quiz.grpc.QuizExerciseDto;
import com.codecampus.quiz.grpc.SubmitQuizRequest;
import com.codecampus.quiz.grpc.SubmitQuizResponse;
import com.codecampus.quiz.grpc.UpsertAssignmentRequest;
import com.codecampus.quiz.helper.AuthenticationHelper;
import com.codecampus.quiz.helper.QuizHelper;
import com.codecampus.quiz.helper.QuizScoringHelper;
import com.codecampus.quiz.mapper.AssignmentMapper;
import com.codecampus.quiz.mapper.QuizMapper;
import com.codecampus.quiz.mapper.SubmissionMapper;
import com.codecampus.quiz.repository.AssignmentRepository;
import com.codecampus.quiz.repository.QuestionRepository;
import com.codecampus.quiz.repository.QuizExerciseRepository;
import com.codecampus.quiz.repository.QuizSubmissionRepository;
import com.codecampus.quiz.service.cache.LoadQuizCacheService;
import com.codecampus.submission.grpc.CreateQuizSubmissionRequest;
import com.codecampus.submission.grpc.SubmissionSyncServiceGrpc;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizService {

    QuizExerciseRepository quizExerciseRepository;
    QuizSubmissionRepository quizSubmissionRepository;
    QuestionRepository questionRepository;
    AssignmentRepository assignmentRepository;

    RedissonClient redisson;

    LoadQuizCacheService loadQuizCacheService;

    AssignmentMapper assignmentMapper;
    QuizMapper quizMapper;
    SubmissionMapper submissionMapper;

    QuizHelper quizHelper;

    SubmissionSyncServiceGrpc.SubmissionSyncServiceBlockingStub submissionStub;

    @Transactional
    public void createQuizExercise(
            CreateQuizExerciseRequest createQuizRequest) {

        QuizExerciseDto exerciseDto = createQuizRequest.getExercise();
        QuizExercise quizExercise = quizExerciseRepository
                .findById(exerciseDto.getId())
                .orElseGet(QuizExercise::new);

        quizMapper.patchQuizExerciseDtoToQuizExercise(
                exerciseDto, quizExercise);
        quizExerciseRepository.save(quizExercise);
    }

    @Transactional
    public void addQuizDetail(
            AddQuizDetailRequest addQuizRequest) {
        QuizExercise quiz =
                quizHelper.findQuizOrThrow(addQuizRequest.getExerciseId());

        addQuizRequest.getQuestionsList().forEach(questionDto -> {
            Question question = quiz.getQuestions()
                    .stream()
                    .filter(q -> q.getId()
                            .equals(questionDto.getId()))
                    .findFirst()
                    .orElseGet(() -> {
                        Question newQuestion =
                                quizMapper.toQuestionFromQuestionDto(
                                        questionDto);


                        newQuestion.setQuiz(quiz);
                        quiz.getQuestions().add(newQuestion);
                        return newQuestion;
                    });
            quizMapper.patchQuestionDtoToQuestion(questionDto, question);

            questionDto.getOptionsList().forEach(optionDto -> {
                Option option = question.getOptions()
                        .stream()
                        .filter(o -> o.getId().equals(optionDto.getId()))
                        .findFirst()
                        .orElseGet(() -> {
                            Option newOption =
                                    quizMapper.toOptionFromOptionDto(optionDto);
                            newOption.setQuestion(question);
                            question.getOptions().add(newOption);
                            return newOption;
                        });
                quizMapper.patchOptionDtoToOption(optionDto, option);
            });

        });
        QuizScoringHelper.recalc(quiz);
        quizExerciseRepository.save(quiz);
        loadQuizCacheService.refresh(
                addQuizRequest.getExerciseId());
    }

    @Transactional
    public void softDeleteExercise(String exerciseId) {
        QuizExercise quizExercise = quizHelper.findQuizOrThrow(exerciseId);
        String by = AuthenticationHelper.getMyUsername();
        quizExercise.markDeleted(by);
        quizExercise.getQuestions().forEach(question -> {
            question.markDeleted(by);
            question.getOptions().forEach(option -> option.markDeleted(by));
        });
        quizExerciseRepository.save(quizExercise);
        loadQuizCacheService.refresh(exerciseId);
    }

    @Transactional
    public void addQuestion(
            AddQuestionRequest addQuestionRequest) {
        QuizExercise quiz =
                quizHelper.findQuizOrThrow(addQuestionRequest.getExerciseId());
        QuestionDto questionDto = addQuestionRequest.getQuestion();

        Question question = quiz.findQuestionById(questionDto.getId())
                .orElseGet(() -> {
                    Question newQuestion = new Question();
                    newQuestion.setId(questionDto.getId());
                    newQuestion.setQuiz(quiz);
                    quiz.getQuestions().add(newQuestion);
                    return newQuestion;
                });

        quizMapper.patchQuestionDtoToQuestion(questionDto, question);

        questionDto.getOptionsList().forEach(optionDto -> {
            Option option = question.findOptionById(optionDto.getId())
                    .orElseGet(() -> {
                        Option newOption = new Option();
                        newOption.setId(optionDto.getId());
                        newOption.setQuestion(question);
                        question.getOptions().add(newOption);
                        return newOption;
                    });

            quizMapper.patchOptionDtoToOption(optionDto, option);
        });

        QuizScoringHelper.recalc(quiz);
        questionRepository.save(question);
        loadQuizCacheService.refresh(addQuestionRequest.getExerciseId());
    }

    @Transactional
    public void softDeleteQuestion(
            String exerciseId,
            String questionId) {

        QuizExercise quizExercise = quizHelper.findQuizOrThrow(exerciseId);
        quizExercise.findQuestionById(questionId).ifPresent(question -> {
            question.markDeleted(AuthenticationHelper.getMyUsername());
            question.getOptions()
                    .forEach(o -> o.markDeleted(question.getDeletedBy()));
        });
    }

    @Transactional
    public void addOption(AddOptionRequest addOptionRequest) {

        Question question =
                quizHelper.findQuestionOrThrow(
                        addOptionRequest.getQuestionId());
        OptionDto optionDto = addOptionRequest.getOption();

        Option option = question.findOptionById(optionDto.getId())
                .orElseGet(() -> {
                    Option newOption = new Option();
                    newOption.setId(optionDto.getId());
                    newOption.setQuestion(question);
                    question.getOptions().add(newOption);
                    return newOption;
                });
        quizMapper.patchOptionDtoToOption(optionDto, option);

        questionRepository.save(question);
        loadQuizCacheService.refresh(addOptionRequest.getExerciseId());
    }


    @Transactional
    public void softDeleteOption(
            String exerciseId,
            String questionId,
            String optionId) {
        QuizExercise quizExercise = quizHelper.findQuizOrThrow(exerciseId);
        quizExercise.findQuestionById(questionId)
                .flatMap(question -> question.findOptionById(optionId))
                .ifPresent(option -> option.markDeleted(
                        AuthenticationHelper.getMyUsername()));
        loadQuizCacheService.refresh(exerciseId);
    }

    @Transactional
    public LoadQuizResponse loadQuiz(
            String exerciseId) {

        String studentId = AuthenticationHelper.getMyUserId();

        if (!assignmentRepository
                .existsByExerciseIdAndStudentId(
                        exerciseId, studentId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // Đọc cache
        LoadQuizResponse loadQuizCached = loadQuizCacheService
                .get(exerciseId);
        if (loadQuizCached != null) {
            return loadQuizCached;
        }

        String lockName = "lock:quiz" + exerciseId;
        RLock lock = redisson.getLock(lockName);

        try {
            // Chặn stampede: chỉ 1 thread/node truy DB
            if (lock.tryLock(2, 10, TimeUnit.SECONDS)) {
                // Re-check sau khi có lock
                loadQuizCached =
                        loadQuizCacheService.get(exerciseId);
                if (loadQuizCached != null) {
                    return loadQuizCached;
                }

                // Truy DB -> build response
                QuizExercise quizExercise = quizHelper
                        .findQuizOrThrow(exerciseId);
                LoadQuizResponse loadQuizResponse =
                        quizMapper.toLoadQuizResponseFromQuizExercise(
                                quizExercise); // Đã ẩn correct

                // Ghi cache
                loadQuizCacheService.put(exerciseId, loadQuizResponse);
                return loadQuizResponse;
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        // Fallback (không lấy được lock)
        QuizExercise quizExercise =
                quizHelper.findQuizOrThrow(exerciseId);

        return quizMapper.toLoadQuizResponseFromQuizExercise(
                quizExercise); // Đã ẩn correct
    }

    @Transactional
    public SubmitQuizResponse submitQuiz(SubmitQuizRequest request) {
        QuizExercise quizExercise =
                quizHelper.findQuizOrThrow(request.getExerciseId());

        QuizSubmission quizSubmission = QuizScoringHelper.score(
                quizExercise, request);

        quizSubmissionRepository.save(quizSubmission);

        // Sync sang submission-service
        submissionStub.createQuizSubmission(
                CreateQuizSubmissionRequest.newBuilder()
                        .setSubmission(
                                submissionMapper.toQuizSubmissionDtoFromQuizSubmission(
                                        quizSubmission))
                        .build()
        );

        return SubmitQuizResponse.newBuilder()
                .setScore(quizSubmission.getScore())
                .setTotalPoints(quizSubmission.getTotalPoints())
                .setPassed(quizSubmission.getScore() ==
                        quizSubmission.getTotalPoints())
                .setTimeTakenSeconds(quizSubmission.getTimeTakenSeconds())
                .build();
    }

    @Transactional
    public void upsertAssignment(
            UpsertAssignmentRequest request) {
        AssignmentDto assignmentDto = request.getAssignment();
        Assignment assignment = assignmentRepository
                .findById(assignmentDto.getId())
                .orElseGet(Assignment::new);

        assignmentMapper.patchAssignmentDtoToAssignment(
                assignmentDto,
                assignment
        );
        assignmentRepository.save(assignment);
    }
}

