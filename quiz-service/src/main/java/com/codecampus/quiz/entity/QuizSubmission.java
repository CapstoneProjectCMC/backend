package com.codecampus.quiz.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "quiz_submission")
public class QuizSubmission
{
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  String exerciseId;
  String studentId;
  Instant submittedAt;
  int score;
  int totalPoints;

  @OneToMany(
      mappedBy = "submission",
      cascade = CascadeType.ALL
  )
  List<QuizSubmissionAnswer> answers = new ArrayList<>();
}
