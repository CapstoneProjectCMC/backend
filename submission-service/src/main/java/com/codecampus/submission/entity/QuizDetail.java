package com.codecampus.submission.entity;

import com.codecampus.submission.entity.audit.AuditMetadata;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
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

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "quiz_detail")
@SQLDelete(sql = "UPDATE quiz_detail " +
        "SET deleted_by = ? , deleted_at = now() " +
        "WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class QuizDetail extends AuditMetadata {
    @Id
    String id;

    @JsonBackReference
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    Exercise exercise;

    @Column(name = "num_questions")
    int numQuestions;

    @Column(name = "total_points")
    int totalPoints;

    @JsonManagedReference
    @OneToMany(mappedBy = "quizDetail",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    List<Question> questions = new ArrayList<>();
}
