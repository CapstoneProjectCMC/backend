package com.codecampus.submission.entity.data;

import java.util.Set;
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
public class AntiCheatConfig {
  boolean shuffleQuestions;
  boolean shuffleOptions;
  boolean fullscreenRequired;
  boolean webcamRequired;
  Integer maxTabSwitches;
  Integer attemptLimitPerExercise;
  Set<String> ipWhitelist;
}
