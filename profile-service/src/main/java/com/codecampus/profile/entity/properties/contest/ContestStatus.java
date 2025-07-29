package com.codecampus.profile.entity.properties.contest;

import com.codecampus.profile.entity.Contest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RelationshipProperties
public class ContestStatus {
    @Id
    @GeneratedValue
    String id;
    String state;   // REGISTERED, SUBMITTED, FINISHED …
    Integer rank;
    Double score;
    Instant updatedAt;
    @TargetNode
    Contest contest;
}
