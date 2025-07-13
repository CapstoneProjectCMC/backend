package com.codecampus.submission.entity;

import com.codecampus.submission.entity.audit.AuditMetadata;
import com.codecampus.submission.entity.data.SubmissionAnswerId;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
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
@Table(name = "submission_answer")
@SQLDelete(sql = "UPDATE submission_answer " +
        "SET deleted_by = ? , deleted_at = now() " +
        "WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class SubmissionAnswer extends AuditMetadata {
    @EmbeddedId
    SubmissionAnswerId id;

    @JsonBackReference
    @MapsId("submissionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    Submission submission;

    @MapsId("questionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    Question question;

    @JoinColumn(name = "selected_option")
    @OneToOne(fetch = FetchType.LAZY)
    Option selectedOption;

    @Column(name = "answer_text", columnDefinition = "text")
    String answerText;

    @Column(name = "is_correct", nullable = false)
    boolean correct;
}
