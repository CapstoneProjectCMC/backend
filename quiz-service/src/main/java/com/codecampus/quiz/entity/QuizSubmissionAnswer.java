package com.codecampus.quiz.entity;

import com.codecampus.quiz.entity.data.QuizSubmissionAnswerId;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "quiz_submission_answer")
public class QuizSubmissionAnswer {
    @EmbeddedId
    QuizSubmissionAnswerId id;

    @JsonBackReference
    @MapsId("quizSubmissionId")
    @ManyToOne
    QuizSubmission submission;

    @MapsId("questionId")
    @ManyToOne
    Question question;

    @ManyToOne
    @JoinColumn(name = "question_option_id",
            foreignKey = @ForeignKey(name = "fk_quiz_answer_option"))
    Option selectedOption;

    String answerText;
    boolean correct;
}
