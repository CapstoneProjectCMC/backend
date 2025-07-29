package com.codecampus.submission.constant.submission;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum SubmissionStatus {
    SUBMITTED,   // 0 – vừa nộp
    PASSED,      // 1 – đạt tối đa điểm
    PARTIAL,     // 2 – có điểm nhưng chưa max
    FAILED       // 3 – 0 điểm
}
