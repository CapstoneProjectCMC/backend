package com.codecampus.submission.helper;

import com.codecampus.submission.dto.response.quiz.QuizAttemptHistoryResponse;
import com.codecampus.submission.entity.Submission;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SubmissionHelper {

    public QuizAttemptHistoryResponse mapSubmissionToQuizAttemptHistoryResponse(
            Submission submission) {
        return new QuizAttemptHistoryResponse(
                submission.getId(),
                submission.getExercise().getId(),
                submission.getExercise().getTitle(),
                submission.getScore(),
                submission.getExercise().getQuizDetail() == null
                        ? null
                        : submission.getExercise().getQuizDetail()
                        .getTotalPoints(),
                submission.getTimeTakenSeconds(),
                submission.getSubmittedAt()
        );
    }
}
