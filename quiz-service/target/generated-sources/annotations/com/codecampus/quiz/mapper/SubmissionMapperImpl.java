package com.codecampus.quiz.mapper;

import com.codecampus.quiz.entity.QuizSubmissionAnswer;
import com.codecampus.quiz.grpc.AnswerDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-04T03:06:18+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.7 (Oracle Corporation)"
)
@Component
public class SubmissionMapperImpl implements SubmissionMapper {

    @Override
    public QuizSubmissionAnswer toQuizSubmissionAnswerFromAnswerDto(AnswerDto answerDto) {
        if ( answerDto == null ) {
            return null;
        }

        QuizSubmissionAnswer.QuizSubmissionAnswerBuilder quizSubmissionAnswer = QuizSubmissionAnswer.builder();

        quizSubmissionAnswer.answerText( answerDto.getAnswerText() );

        return quizSubmissionAnswer.build();
    }
}
