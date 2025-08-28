package com.codecampus.chat.dto.events;

import com.codecampus.chat.entity.ParticipantInfo;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MuteData {
  ParticipantInfo target;
  Instant mutedUntil; // null nếu unmute
}
