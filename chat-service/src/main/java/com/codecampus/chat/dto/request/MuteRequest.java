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
public class MuteRequest {
  Instant mutedUntil; // null hoặc quá khứ => unmute
}