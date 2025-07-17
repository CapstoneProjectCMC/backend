package com.codecampus.submission.helper;

import com.codecampus.submission.constant.sort.SortField;
import com.codecampus.submission.dto.response.quiz.detail.OptionDetailResponse;
import com.codecampus.submission.dto.response.quiz.detail.QuestionDetailResponse;
import com.codecampus.submission.dto.response.quiz.detail.QuizDetailSliceDetailResponse;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Option;
import com.codecampus.submission.entity.Question;
import com.codecampus.submission.entity.QuizDetail;
import com.codecampus.submission.repository.QuestionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class QuizHelper {

    QuestionRepository questionRepository;

    public void recalcQuiz(QuizDetail quizDetail) {
        quizDetail.setTotalPoints(quizDetail.getQuestions().stream()
                .mapToInt(Question::getPoints).sum());
    }

    public QuizDetailSliceDetailResponse buildQuizSliceWithOptions(
            Exercise exercise,
            int qPage, int qSize,
            SortField qSortBy, boolean qAsc) {
        QuizDetail quizDetail = exercise.getQuizDetail();

        if (quizDetail == null) {
            return null;
        }

        Pageable pageable = PageRequest.of(
                qPage - 1,
                qSize,
                SortHelper.build(qSortBy, qAsc));

        Page<Question> pageData =
                questionRepository.findByQuizDetailId(exercise.getId(),
                        pageable);

        List<QuestionDetailResponse> questions = pageData.getContent()
                .stream()
                .map(this::mapQuestionToQuestionDetailResponse)
                .toList();

        return QuizDetailSliceDetailResponse.builder()
                .id(quizDetail.getId())
                .numQuestions(quizDetail.getNumQuestions())
                .totalPoints(quizDetail.getTotalPoints())
                .currentPage(pageData.getNumber() + 1)
                .totalPages(pageData.getTotalPages())
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .questions(questions)
                .createdBy(quizDetail.getCreatedBy())
                .createdAt(quizDetail.getCreatedAt())
                .updatedBy(quizDetail.getUpdatedBy())
                .updatedAt(quizDetail.getUpdatedAt())
                .deletedBy(quizDetail.getDeletedBy())
                .deletedAt(quizDetail.getDeletedAt())
                .build();
    }

    private QuestionDetailResponse mapQuestionToQuestionDetailResponse(
            Question q) {
        return QuestionDetailResponse.builder()
                .id(q.getId())
                .text(q.getText())
                .points(q.getPoints())
                .type(q.getQuestionType().name())
                .orderInQuiz(q.getOrderInQuiz())
                .options(q.getOptions().stream()
                        .sorted(Comparator.comparing(Option::getOrder,
                                Comparator.nullsLast(String::compareTo)))
                        .map(this::mapOptionToOptionDetailResponse)
                        .toList())
                .createdBy(q.getCreatedBy())
                .createdAt(q.getCreatedAt())
                .updatedBy(q.getUpdatedBy())
                .updatedAt(q.getUpdatedAt())
                .deletedBy(q.getDeletedBy())
                .deletedAt(q.getDeletedAt())
                .build();
    }

    private OptionDetailResponse mapOptionToOptionDetailResponse(Option o) {
        return OptionDetailResponse.builder()
                .id(o.getId())
                .optionText(o.getOptionText())
                .correct(o.isCorrect())
                .order(o.getOrder())
                .createdAt(o.getCreatedAt())
                .createdBy(o.getCreatedBy())
                .updatedAt(o.getUpdatedAt())
                .updatedBy(o.getUpdatedBy())
                .deletedBy(o.getDeletedBy())
                .deletedAt(o.getDeletedAt())
                .build();
    }
}
