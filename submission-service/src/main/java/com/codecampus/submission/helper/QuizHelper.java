package com.codecampus.submission.helper;

import com.codecampus.submission.entity.Question;
import com.codecampus.submission.entity.QuizDetail;

public class QuizHelper {
    public static void recalcQuiz(QuizDetail quizDetail) {
        quizDetail.setTotalPoints(quizDetail.getQuestions().stream()
                .mapToInt(Question::getPoints).sum());
    }
}
