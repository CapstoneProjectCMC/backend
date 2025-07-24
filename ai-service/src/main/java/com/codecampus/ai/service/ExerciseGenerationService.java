package com.codecampus.ai.service;

import com.codecampus.ai.constant.exercise.ExerciseType;
import com.codecampus.ai.dto.request.exercise.AddQuizDetailRequest;
import com.codecampus.ai.dto.request.exercise.CreateExerciseRequest;
import com.codecampus.ai.dto.request.exercise.CreateQuizExerciseRequest;
import com.codecampus.ai.dto.request.exercise.ExerciseGenDto;
import com.codecampus.ai.dto.request.exercise.OptionDto;
import com.codecampus.ai.dto.request.exercise.QuestionDto;
import com.codecampus.ai.dto.request.exercise.QuestionGenDto;
import com.codecampus.ai.dto.request.exercise.QuizDetailGenDto;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseGenerationService {

    ChatClient chatClient;

    public ExerciseGenerationService(ChatClient.Builder builder) {

        chatClient = builder.build();
    }

    public CreateExerciseRequest generateExercise(
            Set<String> topics,
            ExerciseType exerciseType) {

        ParameterizedTypeReference<ExerciseGenDto> type =
                new ParameterizedTypeReference<>() {
                };

        ExerciseGenDto suggestion = chatClient
                .prompt()
                .system("""
                        Bạn là trợ lý tạo bài tập lập trình/quiz của CodeCampus.
                        Bài tập là bài tập %s
                        Trả về JSON **đúng theo schema ExerciseGenDto**.
                        Chỉ sinh các trường, không thêm mô tả thừa.
                        """.formatted(exerciseType))
                .user(STR."Chủ đề: \{String.join(", ", topics)}")
                .options(ChatOptions.builder().temperature(0.6).build())
                .advisors(noMemory())
                .call()
                .entity(type);

        return new CreateExerciseRequest(
                suggestion.title(),
                suggestion.description(),
                suggestion.difficulty(),
                suggestion.exerciseType(),
                null, suggestion.cost(), null, null, null,
                suggestion.duration(),
                null, null, null, null
        );
    }

    public AddQuizDetailRequest generateQuizDetail(
            String exerciseTitle,
            String exerciseDescription,
            int numQuestions,
            Set<String> topics) {
        ParameterizedTypeReference<QuizDetailGenDto> type =
                new ParameterizedTypeReference<>() {
                };

        QuizDetailGenDto suggestion = chatClient
                .prompt()
                .system("""
                        Bạn là trợ lý tạo bài tập lập trình/quiz của CodeCampus.
                        Sinh **đúng** JSON QuizDetailGenDto gồm %d câu hỏi.
                        Thông tin bài: "%s" – %s.
                        Chủ đề (gợi ý): %s.
                        Câu hỏi trắc nghiệm đơn hoặc nhiều đáp án.
                        Sử dụng tiếng Việt, tránh lặp lại.
                        """.formatted(
                        numQuestions,
                        exerciseTitle,
                        exerciseDescription == null ? "" : exerciseDescription,
                        String.join(", ", topics)))
                .options(ChatOptions.builder().temperature(0.7).build())
                .advisors(noMemory())
                .advisors(noMemory())
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

    public QuestionDto generateQuestion(
            int orderInQuiz,
            Set<String> topicsQuiz) {

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
                .advisors(noMemory())
                .call()
                .entity(type);

        return new QuestionDto(
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
    }

    public CreateQuizExerciseRequest generateQuizDraft(
            Set<String> exerciseTopics,
            ExerciseType exerciseType,
            int numQuestions) {

        CreateExerciseRequest createExerciseRequest =
                generateExercise(exerciseTopics, exerciseType);

        AddQuizDetailRequest addQuizDetailRequest = generateQuizDetail(
                createExerciseRequest.title(),
                createExerciseRequest.description(),
                numQuestions,
                exerciseTopics
        );

        return new CreateQuizExerciseRequest(
                createExerciseRequest,
                addQuizDetailRequest);
    }

    private Consumer<ChatClient.AdvisorSpec> noMemory() {
        return adv -> adv.param("skipMemory", "true");
    }
}