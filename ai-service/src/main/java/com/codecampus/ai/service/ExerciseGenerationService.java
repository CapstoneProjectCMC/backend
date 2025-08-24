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
import com.codecampus.ai.dto.request.quiz.GenerateQuestionsPromptIn;
import com.codecampus.ai.dto.request.quiz.GenerateQuizPromptIn;
import com.codecampus.ai.dto.request.quiz.OptionDto;
import com.codecampus.ai.dto.request.quiz.OptionGenDto;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

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

  public List<QuestionResponse> generateQuestions(
      GenerateQuestionsPromptIn generateQuestionsPromptIn) {
    int n = (generateQuestionsPromptIn.numQuestions() == null ||
        generateQuestionsPromptIn.numQuestions() < 1)
        ? 1 : generateQuestionsPromptIn.numQuestions();

    ParameterizedTypeReference<QuizDetailGenDto> type =
        new ParameterizedTypeReference<>() {
        };

    // Prompt: yêu cầu đúng N câu, có thể ép type/points nếu được truyền
    QuizDetailGenDto suggestion = chatClient
        .prompt()
        .system("""
            Bạn là trợ lý sinh câu hỏi cho CodeCampus.
            - Sinh JSON QuizDetailGenDto gồm ĐÚNG %d câu (field 'questions').
            - Nếu client cung cấp questionType/points thì mọi câu đều dùng chúng.
            - Cấu trúc mỗi câu tuân thủ QuestionGenDto (có options nếu là dạng chọn).
            - Không sinh trường ngoài schema.
            """.formatted(n))
        .user("""
            Bối cảnh bài tập:
            - title: %s
            - desc: %s
            - diff: %s
            - duration: %s
            - tags: %s
            
            Ràng buộc (tùy chọn):
            - questionType: %s
            - points: %s
            """.formatted(
            safeCheckNullString(generateQuestionsPromptIn.title()),
            safeCheckNullString(
                generateQuestionsPromptIn.description()),
            String.valueOf(generateQuestionsPromptIn.difficulty()),
            String.valueOf(generateQuestionsPromptIn.duration()),
            generateQuestionsPromptIn.tags() == null ? "" :
                String.join(", ",
                    generateQuestionsPromptIn.tags()),
            String.valueOf(
                generateQuestionsPromptIn.questionType()),
            String.valueOf(generateQuestionsPromptIn.points())))
        .options(ChatOptions.builder().temperature(0.6).build())
        .advisors(AIGenerationHelper.noMemory())
        .call()
        .entity(type);

    List<QuestionGenDto> questionGenDtoList =
        suggestion == null || suggestion.questions() == null
            ? List.of() : suggestion.questions();

    // Map sang QuestionDto (ghi đè type/points nếu có trong input)
    List<QuestionDto> questionDtoList =
        new ArrayList<>(questionGenDtoList.size());
    int idx = 1;
    for (QuestionGenDto g : questionGenDtoList) {
      var qType = (generateQuestionsPromptIn.questionType() != null) ?
          generateQuestionsPromptIn.questionType() :
          g.questionType();
      var pts = (generateQuestionsPromptIn.points() != null) ?
          generateQuestionsPromptIn.points() : g.points();

      List<OptionDto> options = g.options() == null ? List.of()
          : g.options().stream()
          .map((OptionGenDto og) ->
              new OptionDto(og.optionText(), og.correct(),
                  og.order()))
          .toList();

      questionDtoList.add(new QuestionDto(
          g.text(),
          qType,
          pts,
          (g.orderInQuiz() == null || g.orderInQuiz() <= 0) ? idx :
              g.orderInQuiz(),
          options
      ));
      idx++;
    }

    // Đẩy từng câu vào submission-service (giống generateTestCases)
    List<QuestionResponse> created =
        new ArrayList<>(questionDtoList.size());
    for (QuestionDto q : questionDtoList) {
      try {
        var resp = submissionClient
            .internalAddQuestion(
                generateQuestionsPromptIn.exerciseId(), q)
            .getResult();
        if (resp != null) {
          created.add(resp);
        }
      } catch (BadRequestException e) {
        // log rồi bỏ qua câu lỗi, tiếp tục các câu còn lại
        log.warn("AddQuestion failed: {}", e.getMessage());
      }
    }
    return created;
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
            nullToString(promptIn.timeLimitMs()),
            nullToString(promptIn.memoryLimitMb()),
            nullToString(promptIn.maxSubmissions()),
            String.join(", ",
                promptIn.preferredLanguages() == null ?
                    Set.of() :
                    promptIn.preferredLanguages())))
        .options(ChatOptions.builder().temperature(0.6).build())
        .advisors(AIGenerationHelper.noMemory())
        .call()
        .entity(type);

    List<TestCaseDto> testCaseDtoList =
        suggestion.testCases() == null ? List.of()
            : suggestion.testCases().stream()
            .map(t -> new TestCaseDto(
                t.input(),
                t.expectedOutput(),
                t.sample(),
                t.note())
            )
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
        testCaseDtoList,
        suggestion.solution()
    );
  }

  public ExerciseResponse generateCodingExercise(
      GenerateCodingPromptIn codingPromptIn) {
    // 1) Sinh phần "khung" Exercise (type=CODING)
    CreateExerciseRequest createExercise =
        generateExercise(
            codingPromptIn.exercisePromptIn(),
            ExerciseType.CODING
        );

    // 2) Sinh CodingDetail + testcases theo mong muốn
    CodingDetailPromptIn codingDetailPromptIn = new CodingDetailPromptIn(
        createExercise,
        codingPromptIn.numTestCases() == null ? 8 :
            codingPromptIn.numTestCases(),
        codingPromptIn.allowedLanguages(),
        codingPromptIn.timeLimit(),
        codingPromptIn.memoryLimit(),
        codingPromptIn.maxSubmissions()
    );
    AddCodingDetailRequest codingDetail =
        generateCodingDetail(codingDetailPromptIn);

    // 3) Gọi Submission để tạo đầy đủ
    CreateCodingExerciseRequest request =
        new CreateCodingExerciseRequest(createExercise, codingDetail);

    return submissionClient.internalCreateCodingExercise(request)
        .getResult();
  }

  public List<TestCaseResponse> generateTestCases(
      GenerateTestCasesPromptIn generateTestCasesPromptIn) {

    ParameterizedTypeReference<CodingDetailGenDto> type =
        new ParameterizedTypeReference<>() {
        };

    CodingDetailGenDto suggestion = chatClient
        .prompt()
        .system("""
            Bạn là trợ lý tạo bài CODE cho CodeCampus.
            Sinh thêm testcases cho bài code hiện có.
            Trả về CodingDetailGenDto nhưng chỉ dùng field testCases.
            Tối thiểu %d testcases, cân bằng dễ/khó, bao phủ biên/edge cases.
            """.formatted(
            Math.max(1, generateTestCasesPromptIn.numTestCases())))
        .user("""
            title="%s"
            description="%s"
            I/O hiện tại:
            input="%s"
            output="%s"
            constraint="%s"
            timeLimit=%s, memoryLimit=%s
            """.formatted(
            generateTestCasesPromptIn.title(),
            generateTestCasesPromptIn.description(),
            safeCheckNullString(generateTestCasesPromptIn.input()),
            safeCheckNullString(generateTestCasesPromptIn.output()),
            safeCheckNullString(
                generateTestCasesPromptIn.constraintText()),
            nullToString(generateTestCasesPromptIn.timeLimit()),
            nullToString(generateTestCasesPromptIn.memoryLimit())))
        .options(ChatOptions.builder().temperature(0.5).build())
        .advisors(AIGenerationHelper.noMemory())
        .call()
        .entity(type);

    List<TestCaseDto> testCaseDtoList =
        suggestion.testCases() == null ? List.of()
            : suggestion.testCases().stream()
            .map(t -> new TestCaseDto(t.input(), t.expectedOutput(),
                t.sample(), t.note()))
            .toList();

    // push từng testcase vào submission
    return testCaseDtoList.stream()
        .map(testCaseDto -> {
          try {
            return submissionClient.internalAddTestCase(
                    generateTestCasesPromptIn.exerciseId(),
                    testCaseDto)
                .getResult();
          } catch (BadRequestException e) {
            throw new RuntimeException(e);
          }
        })
        .toList();
  }


  /* helpers */
  private String nullToString(Object object) {
    return object == null ? "null" : String.valueOf(object);
  }

  private String safeCheckNullString(String s) {
    return s == null ? "" : s;
  }
}