package com.codecampus.submission.dto.data;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OptionData
{
  String id;
  String text;
  boolean correct;
  String order;
}
