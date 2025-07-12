package com.codecampus.submission.entity;

import com.codecampus.submission.constant.submission.QuestionType;
import com.codecampus.submission.entity.audit.AuditMetadata;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "question")
@SQLDelete(sql = "UPDATE question " +
        "SET deleted_by = ? , deleted_at = now() " +
        "WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Question extends AuditMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_detail_id", nullable = false)
    QuizDetail quizDetail;

    @Column(nullable = false, columnDefinition = "text")
    String text;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "question_type", nullable = false, columnDefinition = "smallint")
    QuestionType questionType;

    @Column(nullable = false, columnDefinition = "smallint")
    int points;

    @Column(name = "display_order")
    int orderInQuiz;

    // Optional
    @JsonManagedReference
    @OneToMany(mappedBy = "question",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("order ASC")
    List<Option> options = new ArrayList<>();
}
