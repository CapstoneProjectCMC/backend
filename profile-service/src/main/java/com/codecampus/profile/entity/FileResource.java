package com.codecampus.profile.entity;

import com.codecampus.profile.constant.type.ResourceType;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Node("FileResource")
public class FileResource {
  @Id
  @GeneratedValue(generatorClass = UUIDStringGenerator.class)
  String id;
  String fileId;
  String title;
  @Builder.Default
  ResourceType type = ResourceType.OTHER;
  String mimeType;
  String url;      // link tới storage
  long size;
  Instant uploadedAt;
}
