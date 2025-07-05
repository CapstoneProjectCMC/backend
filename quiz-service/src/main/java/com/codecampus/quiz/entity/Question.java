package com.codecampus.quiz.entity;

import com.codecampus.quiz.constant.submission.QuestionType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
@Table(name = "question")
public class Question
{
  @Id
  String id;

  @ManyToOne
  QuizExercise quiz;
  String text;

  @Enumerated(EnumType.ORDINAL)
  QuestionType questionType;

  int points;
  int orderInQuiz;

  @OneToMany(
      mappedBy = "question",
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  List<Option> options = new ArrayList<>();

  /* ---------- helper ---------- */

  /**
   * Lấy Option theo id trong câu hỏi.
   */
  public Optional<Option> optionById(String optionId)
  {
    return options.stream()
        .filter(option -> option.getId().equals(optionId))
        .findFirst();
  }
}
