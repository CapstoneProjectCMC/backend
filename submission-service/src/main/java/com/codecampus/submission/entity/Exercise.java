package com.codecampus.submission.entity;

import com.codecampus.submission.constant.submission.Difficulty;
import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.entity.audit.AuditMetadata;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
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
@Table(name = "exercise")
@SQLDelete(sql = "UPDATE exercise " +
    "SET deleted_by = ? , deleted_at = now() " +
    "WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Exercise extends AuditMetadata {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @Column(name = "user_id", nullable = false)
  String userId; // người tạo

  @Column(length = 100)
  String title;

  @Column(columnDefinition = "text")
  String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "exercise_type", nullable = false)
  ExerciseType exerciseType;

  @OneToOne(mappedBy = "exercise", cascade = CascadeType.ALL)
  CodingDetail codingDetail;

  @OneToOne(mappedBy = "exercise", cascade = CascadeType.ALL)
  QuizDetail quizDetail;

  @Enumerated(EnumType.ORDINAL)
  @Column(nullable = false, columnDefinition = "smallint default 1")
  Difficulty difficulty = Difficulty.EASY;

  @Column(name = "org_id")
  String orgId;

  @Column(nullable = false)
  @Builder.Default
  boolean visibility = false;

  @Column(name = "is_active", nullable = false)
  boolean active = true;

  @Column(nullable = false)
  BigDecimal cost = BigDecimal.ZERO;

  @Column(name = "free_for_org", nullable = false)
  boolean freeForOrg;

  Instant startTime;
  Instant endTime;

  @Column(columnDefinition = "smallint")
  int duration;

  @Column(name = "allow_discussion_id")
  String allowDiscussionId;

  @Column(name = "resource_ids", columnDefinition = "text[]")
  Set<String> resourceIds;

  @Column(name = "tags", columnDefinition = "text[]")
  Set<String> tags;

  @Builder.Default
  @Column(name = "allow_ai_question", nullable = false)
  boolean allowAiQuestion = false;

  @JsonBackReference
  @ManyToMany(mappedBy = "exercises")
  Set<Contest> contests;

  @JsonBackReference
  @OneToMany(
      mappedBy = "exercise",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY
  )
  @Builder.Default
  Set<Assignment> assignments = new HashSet<>();
}
