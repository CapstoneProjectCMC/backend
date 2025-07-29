package com.codecampus.submission.entity.data;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Embeddable
public class SubmissionAnswerId implements Serializable {
    @Column(name = "submission_id")
    String submissionId;

    @Column(name = "question_id")
    String questionId;
}
