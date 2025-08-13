package com.codecampus.coding.entity;

import com.codecampus.coding.entity.audit.AuditMetadata;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@SQLDelete(sql = "UPDATE code_submission " +
        "SET deleted_by = ? , deleted_at = now() " +
        "WHERE submission_id = ? AND test_case_id = ?")
@Where(clause = "deleted_at IS NULL")
public class CodeSubmission extends AuditMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String studentId;
    String language;

    @Lob
    String sourceCode;

    Instant submittedAt;
    Integer timeTakenSeconds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coding_exercise_id", nullable = false)
    CodingExercise exercise;

    Integer score;              // #passed
    Integer totalPoints;        // #testcases
    boolean passed;

    @OneToMany(mappedBy = "submission",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    List<CodeSubmissionResult> results = new ArrayList<>();
}
