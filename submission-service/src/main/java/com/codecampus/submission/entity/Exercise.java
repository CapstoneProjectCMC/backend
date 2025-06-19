package com.codecampus.submission.entity;

import com.codecampus.submission.constant.submission.Difficulty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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
@Table(name = "exercise")
public class Exercise
{
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @Column(name = "user_id", nullable = false)
  String userId;               // người đăng

  @Column(length = 100, nullable = false)
  String title;

  @Column(columnDefinition = "text")
  String description;

  // liên kết chi tiết code / quiz (One-To-One)
  @OneToOne(mappedBy = "exercise", cascade = CascadeType.ALL)
  CodingDetail codingDetail;

  @OneToOne(mappedBy = "exercise", cascade = CascadeType.ALL)
  QuizDetail quizDetail;

  @Enumerated(EnumType.ORDINAL)
  @Column(nullable = false, columnDefinition = "smallint default 1")
  Difficulty difficulty = Difficulty.EASY;

  @Column(name = "created_by", nullable = false)
  String createdBy;

  @Column(name = "org_id")
  String orgId;                // nullable

  @Column(nullable = false)
  boolean visibility;          // true = public

  @Column(name = "is_active", nullable = false)
  boolean active = true;

  @Column(nullable = false)
  BigDecimal cost = BigDecimal.ZERO;

  @Column(name = "free_for_org", nullable = false)
  boolean freeForOrg;

  @Column(name = "created_at", nullable = false, updatable = false)
  Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  Instant updatedAt;

  Instant startTime;
  Instant endTime;

  @Column(columnDefinition = "smallint")
  int duration;            // phút; nullable

  @Column(name = "allow_discussion_id")
  String allowDiscussionId;

  @Column(name = "resource_ids", columnDefinition = "text[]")
  Set<String> resourceIds;

  @PrePersist
  public void prePersist() {
    Instant now = Instant.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  public void preUpdate() {
    updatedAt = Instant.now();
  }
}
