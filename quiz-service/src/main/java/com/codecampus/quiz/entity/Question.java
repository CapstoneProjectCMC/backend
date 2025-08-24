package com.codecampus.quiz.entity;

import com.codecampus.quiz.constant.submission.QuestionType;
import com.codecampus.quiz.entity.audit.AuditMetadata;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "question")
@SQLDelete(sql = "UPDATE question " +
    "SET deleted_by = ? , deleted_at = now() " +
    "WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Question extends AuditMetadata {
  @Id
  String id;

  @JsonBackReference
  @ManyToOne
  QuizExercise quiz;

  @Column(columnDefinition = "text")
  String text;

  @Enumerated(EnumType.ORDINAL)
  QuestionType questionType;

  int points;
  int orderInQuiz;

  @JsonManagedReference
  @OneToMany(
      mappedBy = "question",
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  @OrderBy("order ASC")
  @Builder.Default
  List<Option> options = new ArrayList<>();

  public Optional<Option> findOptionById(String optionId) {
    return options.stream()
        .filter(o -> !o.isDeleted())
        .filter(o -> o.getId().equals(optionId))
        .findFirst();
  }
}
