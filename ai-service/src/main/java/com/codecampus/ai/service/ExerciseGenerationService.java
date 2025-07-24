package com.codecampus.ai.service;

import com.codecampus.ai.constant.exercise.ExerciseType;
import com.codecampus.ai.dto.request.exercise.AddQuizDetailRequest;
import com.codecampus.ai.dto.request.exercise.CreateExerciseRequest;
import com.codecampus.ai.dto.request.exercise.CreateQuizExerciseRequest;
import com.codecampus.ai.dto.request.exercise.ExerciseGenDto;
import com.codecampus.ai.dto.request.exercise.ExercisePromptIn;
import com.codecampus.ai.dto.request.exercise.GenerateQuizPromptIn;
import com.codecampus.ai.dto.request.exercise.OptionDto;
import com.codecampus.ai.dto.request.exercise.QuestionDto;
import com.codecampus.ai.dto.request.exercise.QuestionGenDto;
import com.codecampus.ai.dto.request.exercise.QuestionPromptIn;
import com.codecampus.ai.dto.request.exercise.QuizDetailGenDto;
import com.codecampus.ai.dto.request.exercise.QuizDetailPromptIn;
import com.codecampus.ai.dto.response.ExerciseResponse;
import com.codecampus.ai.dto.response.QuestionResponse;
import com.codecampus.ai.helper.AIGenerationHelper;
import com.codecampus.ai.mapper.ExerciseMapper;
import com.codecampus.ai.repository.SubmissionClient;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseGenerationService {

    SubmissionClient submissionClient;
    ChatClient chatClient;

    ExerciseMapper exerciseMapper;

    public ExerciseGenerationService(
            ChatClient.Builder builder,
            SubmissionClient submissionClient,
            ExerciseMapper exerciseMapper) {

        chatClient = builder.build();
        this.submissionClient = submissionClient;
        this.exerciseMapper = exerciseMapper;
    }

    public CreateExerciseRequest generateExercise(
            ExercisePromptIn promptIn) {

        ParameterizedTypeReference<ExerciseGenDto> type =
                new ParameterizedTypeReference<>() {
                };

        ExerciseGenDto suggestion = chatClient
                .prompt()
                .system("""
                        Bạn là trợ lý tạo bài tập CodeCampus.
                        Sinh JSON ExerciseGenDto **đúng schema**,
                        KHÔNG thêm trường ngoài schema.
                        """)
                .user("""
                        title="%s"
                        description="%s"
                        difficulty=%s
                        duration=%d
                        tags=%s
                        """.formatted(
                        promptIn.title(),
                        promptIn.description(),
                        promptIn.difficulty(),
                        promptIn.duration(),
                        String.join(", ", promptIn.tags())))
                .options(ChatOptions.builder().temperature(0.6).build())
                .advisors(AIGenerationHelper.noMemory())
                .call()
                .entity(type);

        return new CreateExerciseRequest(
                suggestion.title(),
                suggestion.description(),
                suggestion.difficulty(),
                ExerciseType.QUIZ,
                null, BigDecimal.ZERO, null,
                null, null,
                suggestion.duration(),
                null, null, null, true
        );
    }

    public AddQuizDetailRequest generateQuizDetail(
            QuizDetailPromptIn promptIn) {

        ParameterizedTypeReference<QuizDetailGenDto> type =
                new ParameterizedTypeReference<>() {
                };

        QuizDetailGenDto suggestion = chatClient
                .prompt()
                .system("""
                        Bạn là trợ lý tạo quiz detail của CodeCampus.
                        Sinh QuizDetailGenDto gồm đúng %d câu hỏi.
                        title="%s"
                        description="%s"
                        difficulty=%s
                        duration=%d
                        tags=%s
                        """.formatted(promptIn.numQuestions(),
                        promptIn.createExerciseRequest().title(),
                        promptIn.createExerciseRequest().description(),
                        promptIn.createExerciseRequest().difficulty(),
                        promptIn.createExerciseRequest().duration(),
                        promptIn.createExerciseRequest().tags()))
                .options(ChatOptions.builder().temperature(0.7).build())
                .advisors(AIGenerationHelper.noMemory())
                .call()
                .entity(type);

        List<QuestionDto> questionDtos = suggestion.questions()
                .stream()
                .map(questionGenDto -> new QuestionDto(
                        questionGenDto.text(),
                        questionGenDto.questionType(),
                        questionGenDto.points(),
                        questionGenDto.orderInQuiz(),
                        questionGenDto.options().stream()
                                .map(optionGenDto -> new OptionDto(
                                        optionGenDto.optionText(),
                                        optionGenDto.correct(),
                                        optionGenDto.order()))
                                .toList()
                )).toList();

        return new AddQuizDetailRequest(questionDtos);
    }

    public QuestionResponse generateQuestion(
            QuestionPromptIn promptIn)
            throws BadRequestException {

        ParameterizedTypeReference<QuestionGenDto> type =
                new ParameterizedTypeReference<>() {
                };

        QuestionGenDto suggestion = chatClient
                .prompt()
                .system("""
                        Bạn là trợ lý sinh câu hỏi của CodeCampus
                        Sinh 1 QuestionGenDto (%s) %d điểm.
                        Bối cảnh bài tập:
                        - title: %s
                        - desc: %s
                        - diff: %s
                        - duration: %d
                        - tags: %s
                        """.formatted(promptIn.questionType(),
                        promptIn.points(),
                        promptIn.title(), promptIn.description(),
                        promptIn.difficulty(),
                        promptIn.duration(),
                        String.join(", ", promptIn.tags())))
                .options(ChatOptions.builder().temperature(0.6).build())
                .advisors(AIGenerationHelper.noMemory())
                .call()
                .entity(type);


        QuestionDto questionDto =
                exerciseMapper.mapQuestionGenToQuestionDto(suggestion);

        return submissionClient
                .internalAddQuestion(promptIn.exerciseId(), questionDto)
                .getResult();
    }

    public ExerciseResponse generateQuizExercise(
            GenerateQuizPromptIn generateQuizPromptIn) {

        CreateExerciseRequest createExerciseRequest =
                generateExercise(generateQuizPromptIn.exercisePromptIn());

        AddQuizDetailRequest addQuizDetailRequest = generateQuizDetail(
                new QuizDetailPromptIn(
                        createExerciseRequest,
                        generateQuizPromptIn.numQuestions()
                )
        );

        CreateQuizExerciseRequest createQuizExerciseRequest =
                new CreateQuizExerciseRequest(
                        createExerciseRequest,
                        addQuizDetailRequest);

        return submissionClient
                .internalCreateQuizExercise(createQuizExerciseRequest)
                .getResult();
    }
}