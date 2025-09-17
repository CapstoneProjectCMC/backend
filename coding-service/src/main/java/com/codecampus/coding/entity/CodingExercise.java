package com.codecampus.coding.entity;

import com.codecampus.coding.entity.audit.AuditMetadata;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
@Table(name = "coding_exercise")
@SQLDelete(sql = "UPDATE coding_exercise " +
    "SET deleted_by = ? , deleted_at = now() " +
    "WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class CodingExercise extends AuditMetadata {
  @Id
  String id;

  @Column(length = 100)
  String title;

  @Column(columnDefinition = "text")
  String description;

  @Column(length = 100)
  String topic;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "coding_exercise_allowed_language",
      joinColumns = @JoinColumn(name = "coding_exercise_id"))
  @Column(name = "languages", length = 50)
  @Builder.Default
  Set<String> allowedLanguages = new HashSet<>();

  @Column(columnDefinition = "text")
  String input;

  @Column(columnDefinition = "text")
  String output;

  @Column(name = "constraint_text", columnDefinition = "text")
  String constraintText;

  int timeLimit;
  int memoryLimit;
  int maxSubmissions;

  @Column(name = "code_template", columnDefinition = "text")
  String codeTemplate;

  @Column(columnDefinition = "text")
  String solution;

  boolean publicAccessible;

  @JsonManagedReference
  @OneToMany(mappedBy = "coding",
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  List<TestCase> testCases = new ArrayList<>();

  public Optional<TestCase> findTestCaseById(String testCaseId) {
    return testCases.stream()
        .filter(tc -> tc.getId().equals(testCaseId))
        .findFirst();
  }
}
