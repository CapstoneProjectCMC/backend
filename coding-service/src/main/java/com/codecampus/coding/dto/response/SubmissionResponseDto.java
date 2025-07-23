package com.codecampus.coding.dto.response,

import lombok.*,
import lombok.experimental.FieldDefaults,

@Builder
@FieldDefaults(level = AccessLevel.PUBLIC)
public record SubmissionResponseDto(
   String submissionId,   // ID bài nộp
   String status,         // Kết quả: Accepted, Wrong Answer, Error, Time Limit Exceeded, Compilation Error...
   String actualOutput,   // Kết quả thực tế từ chương trình của thí sinh
   String expectedOutput, // Kết quả mong đợi
   String message,        // Thông điệp phụ: exit code, lỗi biên dịch, lỗi runtime, ...
   long timeUsedMs,       // (tuỳ chọn) thời gian chạy
   long memoryUsedKb      // (tuỳ chọn) bộ nhớ sử dụng
){
}

