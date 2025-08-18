package com.codecampus.ai.service;

import com.codecampus.ai.constant.exercise.ExerciseType;
import com.codecampus.ai.dto.request.coding.AddCodingDetailRequest;
import com.codecampus.ai.dto.request.coding.CodingDetailGenDto;
import com.codecampus.ai.dto.request.coding.CodingDetailPromptIn;
import com.codecampus.ai.dto.request.coding.CreateCodingExerciseRequest;
import com.codecampus.ai.dto.request.coding.GenerateCodingPromptIn;
import com.codecampus.ai.dto.request.coding.GenerateTestCasesPromptIn;
import com.codecampus.ai.dto.request.coding.TestCaseDto;
import com.codecampus.ai.dto.request.exercise.CreateExerciseRequest;
import com.codecampus.ai.dto.request.exercise.ExerciseGenDto;
import com.codecampus.ai.dto.request.exercise.ExercisePromptIn;
import com.codecampus.ai.dto.request.quiz.AddQuizDetailRequest;
import com.codecampus.ai.dto.request.quiz.CreateQuizExerciseRequest;
import com.codecampus.ai.dto.request.quiz.GenerateQuizPromptIn;
import com.codecampus.ai.dto.request.quiz.OptionDto;
import com.codecampus.ai.dto.request.quiz.QuestionDto;
import com.codecampus.ai.dto.request.quiz.QuestionGenDto;
import com.codecampus.ai.dto.request.quiz.QuestionPromptIn;
import com.codecampus.ai.dto.request.quiz.QuizDetailGenDto;
import com.codecampus.ai.dto.request.quiz.QuizDetailPromptIn;
import com.codecampus.ai.dto.response.ExerciseResponse;
import com.codecampus.ai.dto.response.QuestionResponse;
import com.codecampus.ai.dto.response.TestCaseResponse;
import com.codecampus.ai.helper.AIGenerationHelper;
import com.codecampus.ai.mapper.ExerciseMapper;
import com.codecampus.ai.repository.httpClient.SubmissionClient;
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
import java.util.Set;

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
            ExercisePromptIn promptIn,
            ExerciseType exerciseType) {

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
                exerciseType,
                null, BigDecimal.ZERO, null,
                null, null,
                suggestion.duration(),
                null, null, suggestion.tags(), true
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
                        """.formatted(
                        promptIn.questionType(),
                        promptIn.points(),
                        promptIn.title(),
                        promptIn.description(),
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
                generateExercise(
                        generateQuizPromptIn.exercisePromptIn(),
                        ExerciseType.QUIZ);

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


    public AddCodingDetailRequest generateCodingDetail(
            CodingDetailPromptIn promptIn) {
        ParameterizedTypeReference<CodingDetailGenDto> type =
                new ParameterizedTypeReference<>() {
                };

        CodingDetailGenDto suggestion = chatClient
                .prompt()
                .system("""
                        Bạn là trợ lý tạo bài CODE cho CodeCampus.
                        - Sinh JSON CodingDetailGenDto **đúng schema**.
                        - Tối thiểu %d testcases (có cả sample).
                        - Nếu không chắc, đặt sample=false cho phần lớn testcases.
                        - Tránh input/output mơ hồ; rõ ràng, thuần văn bản.
                        """.formatted(
                        Math.max(1, promptIn.numTestCases())))
                .user("""
                        Bối cảnh exercise:
                        title="%s"
                        description="%s"
                        difficulty=%s
                        duration=%d
                        tags=%s
                        
                        Ràng buộc/IO mong muốn:
                        timeLimitMs=%s
                        memoryLimitMb=%s
                        maxSubmissions=%s
                        preferredLanguages=%s
                        """.formatted(
                        promptIn.createExerciseRequest().title(),
                        promptIn.createExerciseRequest().description(),
                        promptIn.createExerciseRequest().difficulty(),
                        promptIn.createExerciseRequest().duration(),
                        String.join(", ",
                                promptIn.createExerciseRequest().tags() ==
                                        null ? List.of() :
                                        promptIn.createExerciseRequest()
                                                .tags()),
                        nullToStr(promptIn.timeLimitMs()),
                        nullToStr(promptIn.memoryLimitMb()),
                        nullToStr(promptIn.maxSubmissions()),
                        String.join(", ",
                                promptIn.preferredLanguages() == null ?
                                        Set.of() :
                                        promptIn.preferredLanguages())))
                .options(ChatOptions.builder().temperature(0.6).build())
                .advisors(AIGenerationHelper.noMemory())
                .call()
                .entity(type);

        List<TestCaseDto> tcs = suggestion.testCases() == null ? List.of()
                : suggestion.testCases().stream()
                .map(t -> new TestCaseDto(t.input(), t.expectedOutput(),
                        t.sample(), t.note()))
                .toList();

        return new AddCodingDetailRequest(
                suggestion.topic(),
                suggestion.allowedLanguages(),
                suggestion.input(),
                suggestion.output(),
                suggestion.constraintText(),
                suggestion.timeLimit(),
                suggestion.memoryLimit(),
                suggestion.maxSubmissions(),
                suggestion.codeTemplate(),
                tcs,
                suggestion.solution()
        );
    }

    public ExerciseResponse generateCodingExercise(GenerateCodingPromptIn in) {
        // 1) Sinh phần "khung" Exercise (type=CODING)
        CreateExerciseRequest createExercise =
                generateExercise(in.exercisePromptIn(), ExerciseType.CODING);

        // 2) Sinh CodingDetail + testcases theo mong muốn
        CodingDetailPromptIn detailPrompt = new CodingDetailPromptIn(
                createExercise,
                in.numTestCases() == null ? 8 : in.numTestCases(),
                in.allowedLanguages(),
                in.timeLimit(),
                in.memoryLimit(),
                in.maxSubmissions()
        );
        AddCodingDetailRequest codingDetail =
                generateCodingDetail(detailPrompt);

        // 3) Gọi Submission để tạo đầy đủ
        CreateCodingExerciseRequest request =
                new CreateCodingExerciseRequest(createExercise, codingDetail);

        return submissionClient.internalCreateCodingExercise(request)
                .getResult();
    }

    public List<TestCaseResponse> generateTestCases(
            GenerateTestCasesPromptIn in)
            throws BadRequestException {

        ParameterizedTypeReference<CodingDetailGenDto> type =
                new ParameterizedTypeReference<>() {
                };

        CodingDetailGenDto suggestion = chatClient
                .prompt()
                .system("""
                        Sinh thêm testcases cho bài code hiện có.
                        Trả về CodingDetailGenDto nhưng chỉ dùng field testCases.
                        Tối thiểu %d testcases, cân bằng dễ/khó, bao phủ biên/edge cases.
                        """.formatted(Math.max(1, in.numTestCases())))
                .user("""
                        title="%s"
                        description="%s"
                        I/O hiện tại:
                        input="%s"
                        output="%s"
                        constraint="%s"
                        timeLimit=%s, memoryLimit=%s
                        """.formatted(
                        in.title(), in.description(),
                        safe(in.input()), safe(in.output()),
                        safe(in.constraintText()),
                        nullToStr(in.timeLimit()), nullToStr(in.memoryLimit())))
                .options(ChatOptions.builder().temperature(0.5).build())
                .advisors(AIGenerationHelper.noMemory())
                .call()
                .entity(type);

        List<TestCaseDto> newOnes = suggestion.testCases() == null ? List.of()
                : suggestion.testCases().stream()
                .map(t -> new TestCaseDto(t.input(), t.expectedOutput(),
                        t.sample(), t.note()))
                .toList();

        // push từng testcase vào submission
        return newOnes.stream()
                .map(tc -> {
                    try {
                        return submissionClient.internalAddTestCase(
                                in.exerciseId(), tc).getResult();
                    } catch (BadRequestException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
    }


    /* helpers */
    private String nullToStr(Object o) {
        return o == null ? "null" : String.valueOf(o);
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}