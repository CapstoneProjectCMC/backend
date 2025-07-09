package com.codecampus.quiz.mapper;

import com.codecampus.quiz.entity.Option;
import com.codecampus.quiz.entity.Question;
import com.codecampus.quiz.entity.QuizExercise;
import com.codecampus.quiz.grpc.OptionDto;
import com.codecampus.quiz.grpc.QuestionDto;
import com.codecampus.quiz.grpc.QuizExerciseDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-09T14:48:40+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class QuizMapperImpl implements QuizMapper {

    @Override
    public void patch(QuestionDto request, Question question) {
        if ( request == null ) {
            return;
        }

        if ( request.getId() != null ) {
            question.setId( request.getId() );
        }
        if ( request.getText() != null ) {
            question.setText( request.getText() );
        }
        if ( request.getQuestionType() != null ) {
            question.setQuestionType( asEntityEnum( request.getQuestionType() ) );
        }
        question.setPoints( request.getPoints() );
        question.setOrderInQuiz( request.getOrderInQuiz() );

        link( question );
    }

    @Override
    public QuizExercise toEntity(QuizExerciseDto dto) {
        if ( dto == null ) {
            return null;
        }

        QuizExercise.QuizExerciseBuilder quizExercise = QuizExercise.builder();

        quizExercise.id( dto.getId() );
        quizExercise.title( dto.getTitle() );
        quizExercise.description( dto.getDescription() );
        quizExercise.totalPoints( dto.getTotalPoints() );
        quizExercise.numQuestions( dto.getNumQuestions() );

        return quizExercise.build();
    }

    @Override
    public Question toEntity(QuestionDto dto) {
        if ( dto == null ) {
            return null;
        }

        Question.QuestionBuilder question = Question.builder();

        question.id( dto.getId() );
        question.text( dto.getText() );
        question.points( dto.getPoints() );
        question.orderInQuiz( dto.getOrderInQuiz() );

        question.questionType( asEntityEnum(dto.getQuestionType()) );

        Question questionResult = question.build();

        link( questionResult );

        return questionResult;
    }

    @Override
    public Option toEntity(OptionDto dto) {
        if ( dto == null ) {
            return null;
        }

        Option.OptionBuilder option = Option.builder();

        option.id( dto.getId() );
        option.optionText( dto.getOptionText() );
        option.order( dto.getOrder() );

        return option.build();
    }
}
