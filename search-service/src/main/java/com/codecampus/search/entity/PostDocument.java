package com.codecampus.search.entity;

import static org.springframework.data.elasticsearch.annotations.FieldType.Date;
import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;
import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(indexName = "posts")
public class PostDocument {

  @Id
  String id;

  @MultiField(
      mainField = @Field(type = Text, analyzer = "icu_analyzer", searchAnalyzer = "icu_analyzer"),
      otherFields = @InnerField(suffix = "keyword", type = Keyword)
  )
  String title;

  @Field(type = Text, analyzer = "icu_analyzer", searchAnalyzer = "icu_analyzer")
  String content;

  @Field(type = Keyword)
  String userId;
  @Field(type = Keyword)
  String orgId;

  @MultiField(
      mainField = @Field(type = Keyword),
      otherFields = @InnerField(suffix = "search", type = Text, analyzer = "icu_analyzer", searchAnalyzer = "icu_analyzer")
  )
  String postType; // Global | Organization | Group

  @Field(type = Keyword)
  String status;
  @Field(type = Keyword)
  String hashtag;

  @Field(type = FieldType.Boolean)
  Boolean isPublic;
  @Field(type = FieldType.Boolean)
  Boolean allowComment;

  @Field(type = Keyword)
  List<String> fileUrls;

  /* Quyền truy cập (để lọc visible giống DB):
     - allowUserIds: các userId được allow (isExcluded == false)
     - excludeUserIds: các userId bị deny (isExcluded == true) */
  @Field(type = Keyword)
  Set<String> allowUserIds;
  @Field(type = Keyword)
  Set<String> excludeUserIds;

  @Field(type = Date)
  Instant createdAt;
  @Field(type = Date)
  Instant updatedAt;
  @Field(type = Date)
  Instant deletedAt;
  @Field(type = Keyword)
  String deletedBy;
}
