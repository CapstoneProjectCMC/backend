package com.codecampus.coding.entity;

import com.codecampus.coding.entity.audit.AuditMetadata;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "test_case")
@SQLDelete(sql = """
        UPDATE test_case
           SET deleted_by = ?, deleted_at = now()
         WHERE id = ?""")
@Where(clause = "deleted_at IS NULL")
public class TestCase extends AuditMetadata {
    @Id
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coding_exercise_id", nullable = false)
    @JsonBackReference
    CodingExercise coding;

    @Column(columnDefinition = "text")
    String input;

    @Column(columnDefinition = "text")
    String expectedOutput;

    boolean sample;

    @Column(columnDefinition = "text")
    String note;
}
