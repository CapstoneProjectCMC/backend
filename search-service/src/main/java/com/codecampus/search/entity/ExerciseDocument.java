package com.codecampus.search.entity;

import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;
import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

import java.time.Instant;
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
@Document(indexName = "exercises")
public class ExerciseDocument {

  @Id
  String id;

  @MultiField(
      mainField = @Field(
          type = Text,
          analyzer = "icu_analyzer",
          searchAnalyzer = "icu_analyzer"
      ),
      otherFields = {@InnerField(
          suffix = "keyword",
          type = Keyword
      )}
  )
  String title;

  @MultiField(
      mainField = @Field(
          type = Text,
          analyzer = "icu_analyzer",
          searchAnalyzer = "icu_analyzer"),
      otherFields = {@InnerField(
          suffix = "keyword",
          type = Keyword
      )}
  )
  String description;

  @MultiField(
      mainField = @Field(type = Keyword),
      otherFields = {
          @InnerField(
              suffix = "search",
              type = Text,
              analyzer = "icu_analyzer",
              searchAnalyzer = "icu_analyzer"
          )
      }
  )
  String exerciseType;

  @MultiField(
      mainField = @Field(type = Keyword),
      otherFields = {@InnerField(
          suffix = "search",
          type = Text,
          analyzer = "icu_analyzer",
          searchAnalyzer = "icu_analyzer")
      }
  )
  Set<String> tags;

  @Field(type = FieldType.Boolean)
  Boolean visibility;

  @Field(type = FieldType.Integer)
  Integer difficulty;

  @Field(type = Keyword)
  String userId;

  @Field(type = Keyword)
  String createdBy;

  @Field(type = FieldType.Double)
  Double cost;

  @Field(type = Keyword)
  String orgId;

  @Field(type = FieldType.Boolean)
  Boolean freeForOrg;

  @Field(type = FieldType.Date)
  Instant startTime;
  @Field(type = FieldType.Date)
  Instant endTime;
  @Field(type = FieldType.Integer)
  Integer duration;

  @Field(type = Keyword)
  Set<String> resourceIds;

  @Field(type = FieldType.Boolean)
  Boolean allowAiQuestion;

  @Field(type = FieldType.Date)
  Instant createdAt;
  @Field(type = FieldType.Date)
  Instant updatedAt;
  @Field(type = FieldType.Date)
  Instant deletedAt;
  @Field(type = Keyword)
  String updatedBy;
  @Field(type = Keyword)
  String deletedBy;
}
