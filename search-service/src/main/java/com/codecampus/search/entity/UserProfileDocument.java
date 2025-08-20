// search-service/src/main/java/com/codecampus/search/entity/UserProfileDocument.java
package com.codecampus.search.entity;

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
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import java.time.Instant;
import java.util.Set;

import static org.springframework.data.elasticsearch.annotations.FieldType.Boolean;
import static org.springframework.data.elasticsearch.annotations.FieldType.Date;
import static org.springframework.data.elasticsearch.annotations.FieldType.Integer;
import static org.springframework.data.elasticsearch.annotations.FieldType.Keyword;
import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(indexName = "userprofiles")
public class UserProfileDocument {

    @Id
    String id;

    /* ---- identity fields ---- */
    @MultiField(
            mainField = @Field(type = Text, analyzer = "icu_analyzer", searchAnalyzer = "icu_analyzer"),
            otherFields = @InnerField(suffix = "keyword", type = Keyword)
    )
    String username;

    @MultiField(
            mainField = @Field(type = Text, analyzer = "icu_analyzer", searchAnalyzer = "icu_analyzer"),
            otherFields = @InnerField(suffix = "keyword", type = Keyword)
    )
    String email;

    @Field(type = Boolean)
    Boolean active;


    @MultiField(
            mainField = @Field(type = Keyword),
            otherFields = @InnerField(suffix = "search", type = Text, analyzer = "icu_analyzer", searchAnalyzer = "icu_analyzer")
    )
    Set<String> roles;

    /* ---- profile fields ---- */
    @Field(type = Text, analyzer = "icu_analyzer", searchAnalyzer = "icu_analyzer")
    String firstName;

    @Field(type = Text, analyzer = "icu_analyzer", searchAnalyzer = "icu_analyzer")
    String lastName;

    @Field(type = Date)
    Instant dob;

    @Field(type = Text, analyzer = "icu_analyzer", searchAnalyzer = "icu_analyzer")
    String bio;

    @Field(type = Boolean)
    Boolean gender;

    @MultiField(
            mainField = @Field(type = Text, analyzer = "icu_analyzer", searchAnalyzer = "icu_analyzer"),
            otherFields = @InnerField(suffix = "keyword", type = Keyword)
    )
    String displayName;

    @Field(type = Integer)
    Integer education;

    @Field(type = Keyword)
    String[] links;

    @MultiField(
            mainField = @Field(type = Text, analyzer = "icu_analyzer", searchAnalyzer = "icu_analyzer"),
            otherFields = @InnerField(suffix = "keyword", type = Keyword)
    )
    String city;

    @Field(type = Keyword)
    String avatarUrl;

    @Field(type = Keyword)
    String backgroundUrl;

    /* ---- audit / soft delete ---- */
    @Field(type = Date)
    Instant createdAt;

    @Field(type = Date)
    Instant updatedAt;

    @Field(type = Date)
    Instant deletedAt;

    @Field(type = Keyword)
    String deletedBy;
}
