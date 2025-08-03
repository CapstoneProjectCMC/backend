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
    date = "2025-08-03T19:12:40+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class QuizMapperImpl implements QuizMapper {

    @Override
    public void patchQuestionDtoToQuestion(QuestionDto questionDto, Question question) {
        if ( questionDto == null ) {
            return;
        }

        if ( questionDto.getId() != null ) {
            question.setId( questionDto.getId() );
        }
        if ( questionDto.getText() != null ) {
            question.setText( questionDto.getText() );
        }
        if ( questionDto.getQuestionType() != null ) {
            question.setQuestionType( mapEntityEnumQuestionType( questionDto.getQuestionType() ) );
        }
        question.setPoints( questionDto.getPoints() );
        question.setOrderInQuiz( questionDto.getOrderInQuiz() );

        linkOptionsToQuestion( question );
    }

    @Override
    public void patchOptionDtoToOption(OptionDto optionDto, Option option) {
        if ( optionDto == null ) {
            return;
        }

        if ( optionDto.getId() != null ) {
            option.setId( optionDto.getId() );
        }
        if ( optionDto.getOptionText() != null ) {
            option.setOptionText( optionDto.getOptionText() );
        }
        option.setCorrect( optionDto.getCorrect() );
        if ( optionDto.getOrder() != null ) {
            option.setOrder( optionDto.getOrder() );
        }
    }

    @Override
    public void patchQuizExerciseDtoToQuizExercise(QuizExerciseDto quizExerciseDto, QuizExercise quizExercise) {
        if ( quizExerciseDto == null ) {
            return;
        }

        if ( quizExerciseDto.getId() != null ) {
            quizExercise.setId( quizExerciseDto.getId() );
        }
        if ( quizExerciseDto.getTitle() != null ) {
            quizExercise.setTitle( quizExerciseDto.getTitle() );
        }
        if ( quizExerciseDto.getDescription() != null ) {
            quizExercise.setDescription( quizExerciseDto.getDescription() );
        }
        quizExercise.setTotalPoints( quizExerciseDto.getTotalPoints() );
        quizExercise.setNumQuestions( quizExerciseDto.getNumQuestions() );
        quizExercise.setDuration( quizExerciseDto.getDuration() );
    }

    @Override
    public QuizExercise toQuizExerciseFromQuizExerciseDto(QuizExerciseDto quizExerciseDto) {
        if ( quizExerciseDto == null ) {
            return null;
        }

        QuizExercise.QuizExerciseBuilder quizExercise = QuizExercise.builder();

        quizExercise.id( quizExerciseDto.getId() );
        quizExercise.title( quizExerciseDto.getTitle() );
        quizExercise.description( quizExerciseDto.getDescription() );
        quizExercise.totalPoints( quizExerciseDto.getTotalPoints() );
        quizExercise.numQuestions( quizExerciseDto.getNumQuestions() );
        quizExercise.duration( quizExerciseDto.getDuration() );

        return quizExercise.build();
    }

    @Override
    public Question toQuestionFromQuestionDto(QuestionDto questionDto) {
        if ( questionDto == null ) {
            return null;
        }

        Question.QuestionBuilder question = Question.builder();

        question.id( questionDto.getId() );
        question.text( questionDto.getText() );
        question.points( questionDto.getPoints() );
        question.orderInQuiz( questionDto.getOrderInQuiz() );

        question.questionType( mapEntityEnumQuestionType(questionDto.getQuestionType()) );

        Question questionResult = question.build();

        linkOptionsToQuestion( questionResult );

        return questionResult;
    }

    @Override
    public Option toOptionFromOptionDto(OptionDto optionDto) {
        if ( optionDto == null ) {
            return null;
        }

        Option.OptionBuilder option = Option.builder();

        option.id( optionDto.getId() );
        option.optionText( optionDto.getOptionText() );
        option.correct( optionDto.getCorrect() );
        option.order( optionDto.getOrder() );

        return option.build();
    }
}
