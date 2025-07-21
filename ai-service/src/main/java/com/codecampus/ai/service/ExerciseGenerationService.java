package com.codecampus.ai.service;

import com.codecampus.ai.dto.request.exercise.AddQuizDetailRequest;
import com.codecampus.ai.dto.request.exercise.CreateExerciseRequest;
import com.codecampus.ai.dto.request.exercise.ExerciseGenDto;
import com.codecampus.ai.dto.request.exercise.OptionDto;
import com.codecampus.ai.dto.request.exercise.QuestionDto;
import com.codecampus.ai.dto.request.exercise.QuestionGenDto;
import com.codecampus.ai.dto.request.exercise.QuizDetailGenDto;
import com.codecampus.ai.dto.request.exercise.QuizDraft;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExerciseGenerationService {

    ChatClient chatClient;
    JdbcChatMemoryRepository jdbcChatMemoryRepository;

    public ExerciseGenerationService(
            ChatClient.Builder builder,
            JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        this.jdbcChatMemoryRepository = jdbcChatMemoryRepository;

        //TODO Nếu mà đầy bộ nhớ thì throw ra lỗi cụ thể
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(100)
                .build();

        chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .build())
                .build();
    }

    public CreateExerciseRequest generateExercise(
            Set<String> topics) {

        ParameterizedTypeReference<ExerciseGenDto> type =
                new ParameterizedTypeReference<>() {
                };

        ExerciseGenDto suggestion = chatClient
                .prompt()
                .system("""
                        Bạn là trợ lý tạo bài tập lập trình/quiz của CodeCampus.
                        Trả về JSON **đúng theo schema ExerciseGenDto**.
                        Chỉ sinh các trường, không thêm mô tả thừa.
                        """)
                .user(topics.toString()) // input tối thiểu
                .options(ChatOptions.builder().temperature(0.6).build())
                .call()
                .entity(type);

        return new CreateExerciseRequest(
                suggestion.title(),
                suggestion.description(),
                suggestion.difficulty(),
                suggestion.exerciseType(),
                null, null, null, null, null,
                suggestion.duration(),
                null, null, null, null
        );
    }

    public AddQuizDetailRequest generateQuizDetail(
            int numQuestions) {
        ParameterizedTypeReference<QuizDetailGenDto> type =
                new ParameterizedTypeReference<>() {
                };

        QuizDetailGenDto suggestion = chatClient
                .prompt()
                .system("""
                        Bạn là trợ lý tạo bài tập lập trình/quiz của CodeCampus.
                        Sinh **đúng** JSON QuizDetailGenDto gồm %d câu hỏi.
                        Câu hỏi trắc nghiệm đơn hoặc nhiều đáp án.
                        Sử dụng tiếng Việt, tránh lặp lại.
                        """.formatted(numQuestions))
                .options(ChatOptions.builder().temperature(0.7).build())
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

    public QuestionDto generateQuestion(int orderInQuiz) {

        ParameterizedTypeReference<QuestionGenDto> type =
                new ParameterizedTypeReference<>() {
                };

        QuestionGenDto suggestion = chatClient
                .prompt()
                .system("""
                        Bạn là trợ lý tạo bài tập lập trình/quiz của CodeCampus.
                        Sinh 1 **QuestionGenDto** (orderInQuiz=%d) kèm 4 option.
                        """.formatted(orderInQuiz))
                .options(ChatOptions.builder().temperature(0.6).build())
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

    public QuizDraft generateQuizDraft(
            Set<String> topics, int numQuestions) {
        return new QuizDraft(
                generateExercise(topics),
                generateQuizDetail(numQuestions));
    }
}