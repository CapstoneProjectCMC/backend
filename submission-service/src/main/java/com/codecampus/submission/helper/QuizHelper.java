package com.codecampus.submission.helper;

import com.codecampus.submission.constant.sort.SortField;
import com.codecampus.submission.dto.common.PageResponse;
import com.codecampus.submission.dto.response.quiz.ExerciseQuizDto;
import com.codecampus.submission.dto.response.quiz.QuizDetailSliceDto;
import com.codecampus.submission.entity.Exercise;
import com.codecampus.submission.entity.Question;
import com.codecampus.submission.entity.QuizDetail;
import com.codecampus.submission.mapper.ExerciseMapper;
import com.codecampus.submission.repository.QuestionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class QuizHelper {

    QuestionRepository questionRepository;

    ExerciseMapper exerciseMapper;

    public void recalcQuiz(QuizDetail quizDetail) {
        quizDetail.setTotalPoints(quizDetail.getQuestions().stream()
                .mapToInt(Question::getPoints).sum());
    }

    public QuizDetailSliceDto buildQuizSlice(
            Exercise exercise,
            int qPage, int qSize,
            SortField qSortBy, boolean qAsc) {

        if (exercise.getQuizDetail() == null) {
            return null;
        }
        Pageable qPageable = PageRequest.of(
                qPage - 1,
                qSize,
                SortHelper.build(qSortBy, qAsc)
        );

        Page<Question> qPageData = questionRepository
                .findByQuizDetailId(
                        exercise.getId(),
                        qPageable
                );

        return exerciseMapper.toQuizDetailSliceDto(
                exercise.getQuizDetail(),
                qPageData,
                questionRepository
        );
    }

    public PageResponse<ExerciseQuizDto> buildPageResponseExerciseQuizDto(
            int exPage,
            int qPage, Integer qSize,
            SortField qSortBy, boolean qAsc, Page<Exercise> exPageData) {

        List<ExerciseQuizDto> exerciseQuizDtoList = exPageData.getContent()
                .stream()
                .map(exercise -> {

                    QuizDetailSliceDto qSlice = null;
                    if (exercise.getQuizDetail() != null) {
                        Pageable qPageable = PageRequest.of(
                                qPage - 1,
                                qSize,
                                SortHelper.build(qSortBy, qAsc)
                        );

                        Page<Question> qPageData = questionRepository
                                .findByQuizDetailId(
                                        exercise.getId(),
                                        qPageable
                                );
                        qSlice = exerciseMapper
                                .toQuizDetailSliceDto(
                                        exercise.getQuizDetail(),
                                        qPageData,
                                        questionRepository
                                );
                    }

                    ExerciseQuizDto exerciseQuizDto = exerciseMapper
                            .toExerciseQuizDto(exercise);

                    return new ExerciseQuizDto(
                            exerciseQuizDto.id(),
                            exerciseQuizDto.title(),
                            exerciseQuizDto.description(),
                            exerciseQuizDto.exerciseType(),
                            exerciseQuizDto.orgId(),
                            exerciseQuizDto.cost(),
                            exerciseQuizDto.freeForOrg(),
                            exerciseQuizDto.tags(),
                            qSlice
                    );
                }).toList();
        return PageResponse.<ExerciseQuizDto>builder()
                .currentPage(exPage)
                .pageSize(exPageData.getSize())
                .totalElements(exPageData.getTotalElements())
                .data(exerciseQuizDtoList)
                .build();
    }
}
