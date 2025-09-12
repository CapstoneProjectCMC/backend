package com.codecampus.search.entity;

import static org.springframework.data.elasticsearch.annotations.FieldType.Date;
import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;
import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Document(indexName = "organizations")
public class OrganizationDocument {

  @Id
  String id;

  @MultiField(
      mainField = @Field(type = Text, analyzer = "icu_analyzer", searchAnalyzer = "icu_analyzer"),
      otherFields = @InnerField(suffix = "keyword", type = Keyword)
  )
  String name;

  @Field(type = Text, analyzer = "icu_analyzer", searchAnalyzer = "icu_analyzer")
  String description;

  @Field(type = Keyword)
  String logoUrl;
  @Field(type = Keyword)
  String email;
  @Field(type = Keyword)
  String phone;

  @Field(type = Text, analyzer = "icu_analyzer", searchAnalyzer = "icu_analyzer")
  String address;

  @MultiField(
      mainField = @Field(type = Keyword),
      otherFields = @InnerField(suffix = "search", type = Text, analyzer = "icu_analyzer", searchAnalyzer = "icu_analyzer")
  )
  String status; // Active/Inactive/Archived

  @Field(type = Date)
  Instant createdAt;
  @Field(type = Date)
  Instant updatedAt;
  @Field(type = Date)
  Instant deletedAt;
  @Field(type = Keyword)
  String deletedBy;
}