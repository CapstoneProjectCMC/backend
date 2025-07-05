package com.codecampus.submission.entity;

import com.codecampus.submission.entity.audit.AuditMetadata;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "coding_detail")
@SQLDelete(sql = "UPDATE coding_detail " +
    "SET deleted_by = ? , deleted_at = now() " +
    "WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class CodingDetail extends AuditMetadata
{
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @JsonBackReference
  @OneToOne
  @MapsId
  @JoinColumn(name = "id")       // FK tới exercise
  Exercise exercise;

  @Column(name = "allowed_languages", nullable = false, columnDefinition = "text[]")
  Set<String> allowedLanguages;

  @Column(name = "input", columnDefinition = "text")
  String input;

  @Column(name = "output", columnDefinition = "text")
  String output;

  @Column(name = "time_limit")
  int timeLimit;

  @Column(name = "topic", length = 100)
  String topic;

  @Column(name = "constraint_text", columnDefinition = "text")
  String constraintText;

  @Column(name = "memory_limit")
  int memoryLimit;

  @Column(name = "max_submissions")
  int maxSubmissions;

  @Column(name = "code_template", columnDefinition = "text")
  String codeTemplate;

  @Column(columnDefinition = "text")
  String solution;

  @JsonManagedReference
  @OneToMany(mappedBy = "codingDetail",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  List<TestCase> testCases = new ArrayList<>();
}
