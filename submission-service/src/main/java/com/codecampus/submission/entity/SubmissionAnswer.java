package com.codecampus.submission.entity;

import com.codecampus.submission.entity.data.SubmissionAnswerId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "submission_answer")
public class SubmissionAnswer {
  @EmbeddedId
  SubmissionAnswerId id;

  // ---- FK tới Submission (dùng submission_id của khóa) ----
  @MapsId("submissionId")                     // trùng field trong Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "submission_id")
  Submission submission;

  // ---- FK tới Question (dùng question_id của khóa) ----
  @MapsId("questionId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id")
  Question question;

  @Column(name = "selected_option")
  String selectedOptionId;   // FK tới Option nếu dạng trắc nghiệm

  @Column(name = "answer_text", columnDefinition = "text")
  String answerText;         // nếu fill-in-blank …

  @Column(name = "is_correct", nullable = false)
  boolean correct;
}
