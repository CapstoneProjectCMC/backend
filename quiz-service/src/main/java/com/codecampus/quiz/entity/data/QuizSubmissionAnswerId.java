package com.codecampus.quiz.entity.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Embeddable
public class QuizSubmissionAnswerId implements Serializable {
  @Column(name = "quiz_submission_id")
  String quizSubmissionId;

  @Column(name = "question_id")
  String questionId;
}
