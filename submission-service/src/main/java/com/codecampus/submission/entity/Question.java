package com.codecampus.submission.entity;

import com.codecampus.submission.constant.submission.QuestionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "question")
public class Question
{
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "exercise_id", nullable = false)
  Exercise exercise;

  @Column(nullable = false, columnDefinition = "text")
  String text;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "question_type", nullable = false, columnDefinition = "smallint")
  QuestionType questionType;

  @Column(nullable = false, columnDefinition = "smallint")
  int points;

  @Column(name = "display_order", columnDefinition = "smallint")
  int order;
}
