package com.codecampus.coding.entity;

import com.codecampus.coding.entity.audit.AuditMetadata;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    String title;
    String description;
    String topic;
    Set<String> allowedLanguages;

    String input;
    String output;

    String constraintText;
    int timeLimit;
    int memoryLimit;
    int maxSubmissions;

    String codeTemplate;
    String solution;

    @JsonManagedReference
    @OneToMany(mappedBy = "exercise",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    List<TestCase> testCases = new ArrayList<>();
}
