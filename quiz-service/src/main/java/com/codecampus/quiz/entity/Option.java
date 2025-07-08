package com.codecampus.quiz.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "question_option")
public class Option {
    @Id
    String id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    Question question;

    @Column(name = "option_text")
    String optionText;
    boolean correct;

    @Column(name = "display_order")
    String order;
}
