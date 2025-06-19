package com.codecampus.submission.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Set;
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
@Table(name = "contest")
public class Contest
{

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @Column(nullable = false, length = 100)
  String title;

  @Column(columnDefinition = "text")
  String description;

  @Column(name = "org_id")
  String orgId;

  @Column(name = "created_by", nullable = false)
  String createdBy;

  @Column(name = "start_time", nullable = false)
  Instant startTime;

  @Column(name = "end_time", nullable = false)
  Instant endTime;

  @Column(name = "is_rank_public", nullable = false)
  boolean rankPublic;

  Instant rankRevealTime;

  // Many-to-many: contest – exercise thông qua bảng nối
  @ManyToMany
  @JoinTable(
      name = "contest_exercise",
      joinColumns = @JoinColumn(name = "contest_id"),
      inverseJoinColumns = @JoinColumn(name = "exercise_id")
  )
  Set<Exercise> exercises;
}
