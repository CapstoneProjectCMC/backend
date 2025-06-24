package com.codecampus.submission.dto.data;

import jakarta.validation.Valid;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizData
{
  @Valid
  List<QuestionData> questions;

  int totalPoints;
}
