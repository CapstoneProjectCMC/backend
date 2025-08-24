package com.codecampus.quiz.entity;

import com.codecampus.quiz.entity.audit.AuditMetadata;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "quiz_exercise")
@SQLDelete(sql = "UPDATE quiz_exercise " +
    "SET deleted_by = ? , deleted_at = now() " +
    "WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class QuizExercise extends AuditMetadata {
  @Id
  String id;

  @Column(length = 100, nullable = false)
  String title;

  @Column(columnDefinition = "text")
  String description;

  int totalPoints;
  int numQuestions;

  int duration;

  boolean publicAccessible;

  @JsonManagedReference
  @OneToMany(
      mappedBy = "quiz",
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  List<Question> questions = new ArrayList<>();

  public Optional<Question> findQuestionById(String questionId) {
    return questions.stream()
        .filter(q -> !q.isDeleted())
        .filter(q -> q.getId().equals(questionId))
        .findFirst();
  }
}
