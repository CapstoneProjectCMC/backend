package com.codecampus.coding.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CodeSubmission {
    @Id
    private String id;
    private String submitedCode;
    private String codedTime;
    private String userId;
    private Instant submittedAt;
    private Integer score;
    @ManyToOne
    private CodingExercise exercise;
}
