package com.codecampus.chat.dto.request;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarkReadRequest {
  String upToMessageId; // tuỳ chọn
  Instant upToTime; // nếu null -> now()
}