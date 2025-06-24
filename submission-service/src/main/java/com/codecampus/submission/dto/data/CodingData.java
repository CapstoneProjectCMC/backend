package com.codecampus.submission.dto.data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CodingData
{
  String id;

  @NotNull
  Set<String> allowedLanguages;

  int timeLimit;
  int memoryLimit;
  int maxSubmissions;

  String codeTemplate;
  String solution;

  @Valid
  List<TestCaseData> testCases;
}
