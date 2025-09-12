package com.codecampus.chat.dto.events;

import com.codecampus.chat.entity.ParticipantInfo;
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
public class MemberLeftData {
  ParticipantInfo leaver;   // người rời nhóm
}