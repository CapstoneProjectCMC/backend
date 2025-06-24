package com.codecampus.submission.dto.request;

import com.codecampus.submission.constant.submission.ExerciseType;
import com.codecampus.submission.dto.data.CodingData;
import com.codecampus.submission.dto.data.QuizData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExerciseCreationRequest
{
  @NotBlank
  String title;
  String description;

  @NotNull
  ExerciseType exerciseType;

  CodingData coding;
  QuizData quiz;
}
