package com.codecampus.submission.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "coding_detail")
public class CodingDetail
{
  @Id
  String id;   // trùng với exercise_id

  @OneToOne
  @MapsId
  @JoinColumn(name = "id")       // FK tới exercise
  Exercise exercise;

  @Column(name = "allowed_languages", nullable = false, columnDefinition = "text[]")
  Set<String> allowedLanguages;

  @Column(name = "time_limit")
  int timeLimit;

  @Column(name = "memory_limit")
  int memoryLimit;

  @Column(name = "max_submissions")
  int maxSubmissions;

  @Column(name = "code_template", columnDefinition = "text")
  String codeTemplate;

  @Column(columnDefinition = "text")
  String solution;
}
