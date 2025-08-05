package com.codecampus.coding.entity;

import com.codecampus.coding.entity.audit.AuditMetadata;
import com.codecampus.coding.entity.data.CodeSubmissionId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
    @EmbeddedId
    CodeSubmissionId id;

    String studentId;
    String language;
    
    @Lob
    String sourceCode;

    Instant submittedAt;
    Integer timeTakenSeconds;

    @MapsId("testCaseId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id")
    TestCase testCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coding_exercise_id", nullable = false)
    CodingExercise exercise;

    boolean passed;
    Integer runtimeMs;
    Integer memoryKb;

    @Lob
    String output;
    @Lob
    String errorMessage;
}
