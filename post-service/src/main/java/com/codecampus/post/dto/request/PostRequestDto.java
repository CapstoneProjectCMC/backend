package com.codecampus.post.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
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
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostRequestDto {
  String title;
  String orgId;
  String content;
  List<String> oldImagesUrls;
  Boolean isPublic;
  Boolean allowComment;
  String postType;
  String hashtag;
  String status;
  AddFileDocumentDto fileDocument;
}
