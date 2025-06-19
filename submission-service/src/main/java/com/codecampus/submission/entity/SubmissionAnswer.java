package com.codecampus.submission.entity;

import com.codecampus.submission.entity.data.SubmissionAnswerId;
import com.codecampus.submission.entity.data.SubmissionResultId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@IdClass(SubmissionAnswerId.class)
public class SubmissionAnswer
{
  @Id
  String submissionId;

  @Id
  String questionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "submission_id", insertable = false, updatable = false)
  Submission submission;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id", insertable = false, updatable = false)
  Question question;

  @Column(name = "selected_option")
  String selectedOptionId;   // FK tới Option nếu dạng trắc nghiệm

  @Column(name = "answer_text", columnDefinition = "text")
  String answerText;         // nếu fill-in-blank …

  @Column(name = "is_correct", nullable = false)
  boolean correct;
}
