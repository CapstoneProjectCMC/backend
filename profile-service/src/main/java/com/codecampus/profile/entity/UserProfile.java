package com.codecampus.profile.entity;

import com.codecampus.profile.entity.properties.contest.ContestStatus;
import com.codecampus.profile.entity.properties.exercise.AssignedOrgExercise;
import com.codecampus.profile.entity.properties.exercise.CompletedExercise;
import com.codecampus.profile.entity.properties.exercise.CreatedExercise;
import com.codecampus.profile.entity.properties.exercise.SavedExercise;
import com.codecampus.profile.entity.properties.organization.CreatedOrg;
import com.codecampus.profile.entity.properties.organization.MemberOrg;
import com.codecampus.profile.entity.properties.post.Reaction;
import com.codecampus.profile.entity.properties.post.ReportedPost;
import com.codecampus.profile.entity.properties.post.SavedPost;
import com.codecampus.profile.entity.properties.post.ViewedPost;
import com.codecampus.profile.entity.properties.resource.ReportedResource;
import com.codecampus.profile.entity.properties.resource.SavedResource;
import com.codecampus.profile.entity.properties.social.Blocks;
import com.codecampus.profile.entity.properties.social.Follows;
import com.codecampus.profile.entity.properties.subcribe.SubscribedTo;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Node("User")
public class UserProfile {
  @Id
  String userId;

  String username;
  String email;

  @Builder.Default
  Boolean active = true;

  @Builder.Default
  Set<String> roles = new HashSet<>();

  String firstName;
  String lastName;
  Instant dob;
  Instant createdAt;
  String avatarUrl;
  String backgroundUrl;
  String bio;
  Boolean gender;
  String displayName;
  Integer education;
  String[] links;
  String city;

  Instant updatedAt;

  // --- SOFT DELETE ---
  Instant deletedAt;
  String deletedBy;

  /* Relationships */

  // Follows / Blocks
  @Relationship(type = "FOLLOWS")
  Set<Follows> follows = new HashSet<>();

  @Relationship(type = "BLOCKS")
  Set<Blocks> blocks = new HashSet<>();

  // Family
  @Relationship(type = "PARENT_OF")
  Set<UserProfile> children = new HashSet<>();

  // Exercise
  @Relationship(type = "SAVED_EXERCISE")
  Set<SavedExercise> savedExercises = new HashSet<>();

  @Relationship(type = "COMPLETED_EXERCISE")
  Set<CompletedExercise> completedExercises = new HashSet<>();

  @Relationship(type = "CREATED_EXERCISE")
  Set<CreatedExercise> createdExercises = new HashSet<>();

  @Relationship(type = "ASSIGNED_ORG_EXERCISE")
  Set<AssignedOrgExercise> exercises = new HashSet<>();

  @Relationship(type = "CONTEST_STATUS")
  Set<ContestStatus> contests = new HashSet<>();

  // Post
  @Relationship(type = "SAVED_POST")
  Set<SavedPost> savedPosts = new HashSet<>();

  @Relationship(type = "VIEWED_POST")
  Set<ViewedPost> viewedPosts = new HashSet<>();

  @Relationship(type = "REACTION")
  Set<Reaction> reactions = new HashSet<>();

  @Relationship(type = "REPORTED_POST")
  Set<ReportedPost> reportedPosts = new HashSet<>();

  // Package
  @Relationship(type = "SUBSCRIBED_TO")
  Set<SubscribedTo> subscriptions = new HashSet<>();

  // Activity
  @Relationship(type = "HAS_ACTIVITY")
  Set<ActivityWeek> activityWeeks = new HashSet<>();

  // Org
  @Relationship(type = "MEMBER_ORG")
  Set<MemberOrg> memberOrgs = new HashSet<>();

  @Relationship(type = "CREATED_ORG")
  Set<CreatedOrg> createdOrgs = new HashSet<>();

  // Resource
  @Relationship(type = "SAVED_RESOURCE")
  Set<SavedResource> savedResources = new HashSet<>();

  @Relationship(type = "REPORTED_RESOURCE")
  Set<ReportedResource> reportedResources = new HashSet<>();
}
