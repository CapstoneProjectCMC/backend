package com.codecampus.submission.entity;

import com.codecampus.submission.entity.audit.AuditMetadata;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@SQLDelete(sql = "UPDATE test_case " +
        "SET deleted_by = ? , deleted_at = now() " +
        "WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class TestCase extends AuditMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coding_detail_id", nullable = false)
    CodingDetail codingDetail;

    @Column(nullable = false, columnDefinition = "text")
    String input;

    @Column(name = "expected_output", nullable = false, columnDefinition = "text")
    String expectedOutput;

    @Column(name = "is_sample", nullable = false)
    boolean sample;

    @Column(columnDefinition = "text")
    String note;
}
