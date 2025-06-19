package com.codecampus.submission.entity;

import com.codecampus.submission.entity.data.ContestRankId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
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
@Table(name = "contest_ranking")
@IdClass(ContestRankId.class)
public class ContestRanking
{
  @Id
  String contestId;

  @Id
  String userId;

  @Column(nullable = false, columnDefinition = "smallint")
  Integer score;

  @Column(nullable = false, columnDefinition = "smallint")
  Integer rank;
}
