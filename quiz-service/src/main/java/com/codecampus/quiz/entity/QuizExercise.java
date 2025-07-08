package com.codecampus.quiz.entity;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "quiz_exercise")
public class QuizExercise {
    @Id
    String id;

    String title;
    String description;
    int totalPoints;
    int numQuestions;

    @JsonManagedReference
    @OneToMany(
            mappedBy = "quiz",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<Question> questions = new ArrayList<>();

    /* ---------- helper  ---------- */

    /**
     * Tìm câu hỏi theo id.
     * Trả về Optional.empty() nếu không thấy.
     */
    public Optional<Question> findQuestion(String questionId) {
        return questions.stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst();
    }
}
