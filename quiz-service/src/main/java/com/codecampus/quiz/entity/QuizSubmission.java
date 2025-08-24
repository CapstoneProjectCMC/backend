package com.codecampus.quiz.entity;

import com.codecampus.quiz.entity.audit.AuditMetadata;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "quiz_submission")
@SQLDelete(sql = "UPDATE quiz_submission " +
    "SET deleted_by = ? , deleted_at = now() " +
    "WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class QuizSubmission extends AuditMetadata {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  String exerciseId;
  String studentId;
  Instant submittedAt;
  int score;
  int totalPoints;

  int timeTakenSeconds;

  @Builder.Default
  @JsonManagedReference
  @OneToMany(
      mappedBy = "submission",
      cascade = CascadeType.ALL
  )
  List<QuizSubmissionAnswer> answers = new ArrayList<>();
}
