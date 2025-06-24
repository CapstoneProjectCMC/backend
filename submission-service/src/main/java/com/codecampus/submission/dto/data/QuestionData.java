package com.codecampus.submission.dto.data;

import com.codecampus.submission.constant.submission.QuestionType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionData
{
  String id;
  String text;
  QuestionType questionType;
  int points;
  int order;
}
