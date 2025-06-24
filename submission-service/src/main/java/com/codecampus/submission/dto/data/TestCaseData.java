package com.codecampus.submission.dto.data;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestCaseData
{
  String id;
  String input;
  String expectedOutput;
  boolean sample;
  String note;
}
