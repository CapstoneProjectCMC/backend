package com.codecampus.submission.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
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
@Table(name = "test_case")
public class TestCase
{
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "exercise_id", nullable = false)
  Exercise exercise;

  @Column(nullable = false, columnDefinition = "text")
  String input;

  @Column(name = "expected_output", nullable = false, columnDefinition = "text")
  String expectedOutput;

  @Column(name = "is_sample", nullable = false)
  boolean sample;

  @Column(name = "created_at", nullable = false, updatable = false)
  Instant createdAt;

  @Column(columnDefinition = "text")
  String note;

  @PrePersist
  void prePersist() {
    createdAt = Instant.now();
  }
}
