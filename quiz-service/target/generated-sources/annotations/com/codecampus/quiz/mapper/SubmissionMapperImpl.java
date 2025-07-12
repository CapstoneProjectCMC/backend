package com.codecampus.quiz.mapper;

import com.codecampus.quiz.entity.Option;
import com.codecampus.quiz.entity.Question;
import com.codecampus.quiz.entity.QuizSubmission;
import com.codecampus.quiz.entity.QuizSubmissionAnswer;
import com.codecampus.submission.grpc.QuizSubmissionAnswerDto;
import com.codecampus.submission.grpc.QuizSubmissionDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-12T18:29:44+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class SubmissionMapperImpl implements SubmissionMapper {

    @Override
    public QuizSubmissionDto toGrpc(QuizSubmission quizSubmission) {
        if ( quizSubmission == null ) {
            return null;
        }

        QuizSubmissionDto.Builder quizSubmissionDto = QuizSubmissionDto.newBuilder();

        quizSubmissionDto.setId( quizSubmission.getId() );
        quizSubmissionDto.setExerciseId( quizSubmission.getExerciseId() );
        quizSubmissionDto.setStudentId( quizSubmission.getStudentId() );
        quizSubmissionDto.setScore( quizSubmission.getScore() );
        quizSubmissionDto.setTotalPoints( quizSubmission.getTotalPoints() );
        quizSubmissionDto.setSubmittedAt( map( quizSubmission.getSubmittedAt() ) );

        return quizSubmissionDto.build();
    }

    @Override
    public QuizSubmissionAnswerDto toGrpc(QuizSubmissionAnswer e) {
        if ( e == null ) {
            return null;
        }

        QuizSubmissionAnswerDto.Builder quizSubmissionAnswerDto = QuizSubmissionAnswerDto.newBuilder();

        quizSubmissionAnswerDto.setQuestionId( eQuestionId( e ) );
        quizSubmissionAnswerDto.setSelectedOptionId( eSelectedOptionId( e ) );
        quizSubmissionAnswerDto.setAnswerText( e.getAnswerText() );
        quizSubmissionAnswerDto.setCorrect( e.isCorrect() );

        return quizSubmissionAnswerDto.build();
    }

    private String eQuestionId(QuizSubmissionAnswer quizSubmissionAnswer) {
        Question question = quizSubmissionAnswer.getQuestion();
        if ( question == null ) {
            return null;
        }
        return question.getId();
    }

    private String eSelectedOptionId(QuizSubmissionAnswer quizSubmissionAnswer) {
        Option selectedOption = quizSubmissionAnswer.getSelectedOption();
        if ( selectedOption == null ) {
            return null;
        }
        return selectedOption.getId();
    }
}
