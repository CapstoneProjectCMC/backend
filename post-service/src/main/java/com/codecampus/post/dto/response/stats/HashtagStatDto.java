package com.codecampus.post.dto.response.stats;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HashtagStatDto {
  String tag;      // hashtag đã normalize (không kèm '#')
  long postCount;  // số bài có chứa tag này
}