package com.codecampus.ai.service;

import com.codecampus.ai.dto.request.exercise.AddQuizDetailRequest;
import com.codecampus.ai.dto.request.exercise.CreateExerciseRequest;
import com.codecampus.ai.dto.request.exercise.CreateQuizExerciseRequest;
import com.codecampus.ai.dto.request.exercise.ExerciseGenDto;
import com.codecampus.ai.dto.request.exercise.ExercisePromptIn;
import com.codecampus.ai.dto.request.exercise.OptionDto;
import com.codecampus.ai.dto.request.exercise.QuestionDto;
import com.codecampus.ai.dto.request.exercise.QuestionGenDto;
import com.codecampus.ai.dto.request.exercise.QuizDetailGenDto;
import com.codecampus.ai.dto.request.exercise.QuizDetailPromptIn;
import com.codecampus.ai.dto.response.ExerciseResponse;
import com.codecampus.ai.dto.response.QuestionResponse;
import com.codecampus.ai.helper.AIGenerationHelper;
import com.codecampus.ai.repository.SubmissionClient;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseGenerationService {

    SubmissionClient submissionClient;

    ChatClient chatClient;

    public ExerciseGenerationService(
            ChatClient.Builder builder,
            SubmissionClient submissionClient1) {

        chatClient = builder.build();
        this.submissionClient = submissionClient1;
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
                suggestion.exerciseType(),
                null, null, null,
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
                        Bạn là trợ lý tạo quiz detail.
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
            String exerciseId,
            int orderInQuiz,
            Set<String> topicsQuiz)
            throws BadRequestException {

        ParameterizedTypeReference<QuestionGenDto> type =
                new ParameterizedTypeReference<>() {
                };

        QuestionGenDto suggestion = chatClient
                .prompt()
                .system("""
                        Bạn là trợ lý tạo bài tập lập trình/quiz của CodeCampus.
                        Sinh 1 **QuestionGenDto** (orderInQuiz=%d) kèm 4 option.
                        Chủ đề quiz: %s.
                        """.formatted(
                        orderInQuiz,
                        String.join(", ", topicsQuiz)))
                .options(ChatOptions.builder().temperature(0.6).build())
                .advisors(AIGenerationHelper.noMemory())
                .call()
                .entity(type);

        QuestionDto questionDto = new QuestionDto(
                suggestion.text(),
                suggestion.questionType(),
                suggestion.points(),
                suggestion.orderInQuiz(),
                suggestion.options().stream()
                        .map(optionGenDto -> new OptionDto(
                                optionGenDto.optionText(),
                                optionGenDto.correct(),
                                optionGenDto.order()))
                        .toList());

        return submissionClient
                .internalAddQuestion(exerciseId, questionDto)
                .getResult();
    }

    public ExerciseResponse generateQuizExercise(
            ExercisePromptIn exercisePromptIn,
            int numQuestions) {

        CreateExerciseRequest createExerciseRequest =
                generateExercise(exercisePromptIn);

        AddQuizDetailRequest addQuizDetailRequest = generateQuizDetail(
                new QuizDetailPromptIn(
                        createExerciseRequest, numQuestions
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