package com.codecampus.search.constant.submission;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SubmissionStatus {
  PENDING(1),
  GRADED(2),
  ERROR(3),
  ACCEPTED(4),
  WRONG_ANSWER(5),
  EVALUATED(6);
  int code;
}
