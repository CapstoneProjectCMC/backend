package com.codecampus.profile.repository;

import com.codecampus.profile.entity.ActivityWeek;
import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.entity.properties.contest.ContestStatus;
import com.codecampus.profile.entity.properties.exercise.CompletedExercise;
import com.codecampus.profile.entity.properties.exercise.CreatedExercise;
import com.codecampus.profile.entity.properties.exercise.SavedExercise;
import com.codecampus.profile.entity.properties.organization.CreatedOrg;
import com.codecampus.profile.entity.properties.organization.MemberOrg;
import com.codecampus.profile.entity.properties.post.Reaction;
import com.codecampus.profile.entity.properties.post.ReportedPost;
import com.codecampus.profile.entity.properties.post.SavedPost;
import com.codecampus.profile.entity.properties.resource.SavedResource;
import com.codecampus.profile.entity.properties.social.Blocks;
import com.codecampus.profile.entity.properties.social.Follows;
import com.codecampus.profile.entity.properties.subcribe.SubscribedTo;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository
    extends Neo4jRepository<UserProfile, String> {

  Optional<UserProfile> findByUserId(String userId);

  @Query("""
      MATCH (u:User {userId:$userId})
      WHERE u.deletedAt IS NULL
      RETURN u
      LIMIT 1
      """)
  Optional<UserProfile> findActiveByUserId(String userId);

  boolean existsByUserId(String userId);

  /* ========================= EXERCISE ========================= */

  @Query(value = """
      MATCH (u:User {userId:$userId})
      WHERE u.deletedAt IS NULL
      MATCH (u)-[completed:COMPLETED_EXERCISE]->(e:Exercise)
      RETURN completed, e AS exercise
      ORDER BY completed.completedAt DESC
      SKIP $skip
      LIMIT $limit
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})
          WHERE u.deletedAt IS NULL
          MATCH (u)-[completed:COMPLETED_EXERCISE]->(:Exercise)
          RETURN count(completed)
          """)
  Page<CompletedExercise> findCompletedExercises(
      String userId,
      Pageable pageable);

  // SDN6 sẽ để trống @TargetNode
  // nếu biến RETURN trong Cypher không trùng tên
  // với tên field trong lớp @RelationshipProperties.
  // Ví dụ CreatedExercise có field Exercise exercise;
  // thì trong query phải RETURN created, e AS exercise
  // (không phải RETURN created, e).
  @Query(value = """
      MATCH (u:User {userId:$userId})
      WHERE u.deletedAt IS NULL
      MATCH (u)-[saved:SAVED_EXERCISE]->(e:Exercise)
      RETURN saved, e AS exercise
      ORDER BY saved.saveAt DESC
      SKIP $skip
      LIMIT $limit
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})
          WHERE u.deletedAt IS NULL
          MATCH (u)-[saved:SAVED_EXERCISE]->(:Exercise)
          RETURN count(saved)
          """)
  Page<SavedExercise> findSavedExercises(
      String userId,
      Pageable pageable);

  @Query(value = """
      MATCH (u:User {userId:$userId})
      WHERE u.deletedAt IS NULL
      MATCH (u)-[created:CREATED_EXERCISE]->(e:Exercise)
      RETURN created, e AS exercise
      ORDER BY e.title
      SKIP $skip
      LIMIT $limit
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})
          WHERE u.deletedAt IS NULL
          MATCH (u)-[created:CREATED_EXERCISE]->(:Exercise)
          RETURN count(created)
          """)
  Page<CreatedExercise> findCreatedExercises(
      String userId,
      Pageable pageable);

  /* ========================= CONTEST ========================= */

  @Query(value = """
      MATCH (u:User {userId:$userId})
      WHERE u.deletedAt IS NULL
      MATCH (u)-[cs:CONTEST_STATUS]->(c:Contest)
      RETURN cs, c AS contest
      ORDER BY cs.updatedAt DESC
      SKIP $skip
      LIMIT $limit
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})
          WHERE u.deletedAt IS NULL
          MATCH (u)-[cs:CONTEST_STATUS]->(:Contest)
          RETURN count(cs)
          """)
  Page<ContestStatus> findContestStatuses(
      String userId,
      Pageable pageable);

  /* ========================= POST ========================= */

  @Query(value = """
      MATCH (u:User {userId:$userId})
      WHERE u.deletedAt IS NULL
      MATCH (u)-[sp:SAVED_POST]->(p:Post)
      RETURN sp, p AS post
      ORDER BY sp.saveAt DESC
      SKIP $skip
      LIMIT $limit
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})
          WHERE u.deletedAt IS NULL
          MATCH (u)-[sp:SAVED_POST]->(:Post)
          RETURN count(sp)
          """)
  Page<SavedPost> findSavedPosts(
      String userId,
      Pageable pageable);

  @Query(value = """
      MATCH (u:User {userId:$userId})
      WHERE u.deletedAt IS NULL
      MATCH (u)-[r:REACTION]->(p:Post)
      RETURN r, p AS post
      ORDER BY r.at DESC
      SKIP $skip
      LIMIT $limit
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})
          WHERE u.deletedAt IS NULL
          MATCH (u)-[r:REACTION]->(:Post)
          RETURN count(r)
          """)
  Page<Reaction> findReactions(
      String userId,
      Pageable pageable);

  @Query(value = """
      MATCH (u:User {userId:$userId})
      WHERE u.deletedAt IS NULL
      MATCH (u)-[rp:REPORTED_POST]->(p:Post)
      RETURN rp, p AS post
      ORDER BY rp.reportedAt DESC
      SKIP $skip
      LIMIT $limit
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})
          WHERE u.deletedAt IS NULL
          MATCH (u)-[rp:REPORTED_POST]->(:Post)
          RETURN count(rp)
          """)
  Page<ReportedPost> findReportedPosts(
      String userId,
      Pageable pageable);

  @Query("""
        MATCH (u:User {userId:$userId})
        WHERE u.deletedAt IS NULL
        MATCH (e:Exercise {exerciseId:$exerciseId})
        MERGE (u)-[r:SAVED_EXERCISE]->(e)
        ON CREATE SET r.saveAt = $now
        ON MATCH  SET r.saveAt = coalesce(r.saveAt, $now)
      """)
  void mergeSavedExercise(String userId, String exerciseId, Instant now);

  @Query("""
        MATCH (u:User {userId:$userId})-[r:SAVED_EXERCISE]->(e:Exercise {exerciseId:$exerciseId})
        DELETE r
      """)
  void deleteSavedExercise(String userId, String exerciseId);

  /* --- POST SAVED / REPORTED --- */
  @Query("""
        MATCH (u:User {userId:$userId})
        WHERE u.deletedAt IS NULL
        MATCH (p:Post {postId:$postId})
        MERGE (u)-[r:SAVED_POST]->(p)
        ON CREATE SET r.saveAt = $now
        ON MATCH  SET r.saveAt = coalesce(r.saveAt, $now)
      """)
  void mergeSavedPost(String userId, String postId, Instant now);

  @Query("""
        MATCH (u:User {userId:$userId})-[r:SAVED_POST]->(p:Post {postId:$postId})
        DELETE r
      """)
  void deleteSavedPost(String userId, String postId);

  @Query("""
        MATCH (u:User {userId:$userId})
        WHERE u.deletedAt IS NULL
        MATCH (p:Post {postId:$postId})
        MERGE (u)-[r:REPORTED_POST]->(p)
        SET r.reason = $reason, r.reportedAt = $at
      """)
  void mergeReportedPost(String userId, String postId, String reason,
                         Instant at);

  @Query("""
        MATCH (u:User {userId:$userId})-[r:REPORTED_POST]->(p:Post {postId:$postId})
        DELETE r
      """)
  void deleteReportedPost(String userId, String postId);


  /* ========================= ACTIVITY TIME ========================= */

  @Query(value = """
      MATCH (u:User {userId:$userId})
      WHERE u.deletedAt IS NULL
      MATCH (u)-[:HAS_ACTIVITY]->(a:ActivityWeek)
      RETURN a
      ORDER BY a.weekStart DESC
      SKIP $skip
      LIMIT $limit
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})
          WHERE u.deletedAt IS NULL
          MATCH (u)-[:HAS_ACTIVITY]->(a:ActivityWeek)
          RETURN count(a)
          """)
  Page<ActivityWeek> findActivityWeek(
      String userId,
      Pageable pageable);

  /* ========================= FAMILY: PARENT_OF ========================= */
  @Query("""
        MATCH (p:User {userId:$parent}) WHERE p.deletedAt IS NULL
        MATCH (c:User {userId:$child})  WHERE c.deletedAt IS NULL
        MERGE (p)-[:PARENT_OF]->(c)
      """)
  void mergeParentChild(String parent, String child);

  @Query("""
        MATCH (p:User {userId:$parent})-[r:PARENT_OF]->(c:User {userId:$child})
        DELETE r
      """)
  void deleteParentChild(String parent, String child);

  /* ========================= FOLLOW / BLOCK ========================= */

  @Query(value = """
      MATCH (me:User {userId:$userId})
      WHERE me.deletedAt IS NULL
      MATCH (me)-[f:FOLLOWS]->(target:User)
      WHERE target.deletedAt IS NULL
        AND NOT EXISTS( (me)-[:BLOCKS]->(target) )
        AND NOT EXISTS( (target)-[:BLOCKS]->(me) )
      RETURN f, target  // target đã trùng tên field @TargetNode
      ORDER BY f.since DESC
      SKIP $skip
      LIMIT $limit
      """,
      countQuery = """
          MATCH (me:User {userId:$userId})
          WHERE me.deletedAt IS NULL
          MATCH (me)-[f:FOLLOWS]->(target:User)
          WHERE target.deletedAt IS NULL
            AND NOT EXISTS( (me)-[:BLOCKS]->(target) )
            AND NOT EXISTS( (target)-[:BLOCKS]->(me) )
          RETURN count(f)
          """)
  Page<Follows> findFollowings(
      String userId,
      Pageable pageable);

  @Query(value = """
      MATCH (me:User {userId:$userId})
      WHERE me.deletedAt IS NULL
      MATCH (src:User)-[f:FOLLOWS]->(me)
      WHERE src.deletedAt IS NULL
        AND NOT EXISTS( (me)-[:BLOCKS]->(src) )
        AND NOT EXISTS( (src)-[:BLOCKS]->(me) )
      RETURN f, src AS target   // quan trọng: alias về đúng tên field
      ORDER BY f.since DESC
      SKIP $skip
      LIMIT $limit
      """,
      countQuery = """
          MATCH (me:User {userId:$userId})
          WHERE me.deletedAt IS NULL
          MATCH (src:User)-[f:FOLLOWS]->(me)
          WHERE src.deletedAt IS NULL
            AND NOT EXISTS( (me)-[:BLOCKS]->(src) )
            AND NOT EXISTS( (src)-[:BLOCKS]->(me) )
          RETURN count(f)
          """)
  Page<Follows> findFollowers(
      String userId,
      Pageable pageable);

  @Query(value = """
      MATCH (me:User {userId:$userId})
      WHERE me.deletedAt IS NULL
      MATCH (me)-[b:BLOCKS]->(target:User)
      WHERE target.deletedAt IS NULL
      RETURN b, target
      ORDER BY b.since DESC
      SKIP $skip
      LIMIT $limit
      """,
      countQuery = """
          MATCH (me:User {userId:$userId})
          WHERE me.deletedAt IS NULL
          MATCH (me)-[b:BLOCKS]->(target:User)
          WHERE target.deletedAt IS NULL
          RETURN count(b)
          """)
  Page<Blocks> findBlocked(
      String userId,
      Pageable pageable);

  @Query("""
        MATCH (me:User {userId:$me})
        WHERE me.deletedAt IS NULL
        MATCH (target:User {userId:$target})
        WHERE target.deletedAt IS NULL
        MERGE (me)-[r:FOLLOWS]->(target)
        ON CREATE SET r.since = $since
      """)
  void mergeFollow(String me, String target, Instant since);

  @Query("""
        MATCH (me:User {userId:$me})-[r:FOLLOWS]->(target:User {userId:$target})
        DELETE r
      """)
  void deleteFollow(String me, String target);

  @Query("""
        MATCH (me:User {userId:$me})
        WHERE me.deletedAt IS NULL
        MATCH (target:User {userId:$target})
        WHERE target.deletedAt IS NULL
        MERGE (me)-[r:BLOCKS]->(target)
        ON CREATE SET r.since = $since
      """)
  void mergeBlock(String me, String target, Instant since);

  @Query("""
        MATCH (me:User {userId:$me})-[r:BLOCKS]->(target:User {userId:$target})
        DELETE r
      """)
  void deleteBlock(String me, String target);

  /* ========================= ORG ========================= */

  @Query(value = """
      MATCH (u:User {userId:$userId})
      WHERE u.deletedAt IS NULL
      MATCH (u)-[m:MEMBER_ORG]->(o:Organization)
      RETURN m, o AS organization
      ORDER BY m.joinAt DESC
      SKIP $skip
      LIMIT $limit
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})
          WHERE u.deletedAt IS NULL
          MATCH (u)-[m:MEMBER_ORG]->(:Organization)
          RETURN count(m)
          """)
  Page<MemberOrg> findMemberOrgs(
      String userId,
      Pageable pageable);

  @Query(value = """
      MATCH (u:User {userId:$userId})
      WHERE u.deletedAt IS NULL
      MATCH (u)-[m:MEMBER_ORG]->(o:Organization)
      WHERE m.memberRole = $role
      RETURN m, o AS organization
      ORDER BY m.joinAt DESC
      SKIP $skip
      LIMIT $limit
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})
          WHERE u.deletedAt IS NULL
          MATCH (u)-[m:MEMBER_ORG]->(:Organization)
          WHERE m.memberRole = $role
          RETURN count(m)
          """)
  Page<MemberOrg> findMemberOrgsByRole(
      String userId,
      String role,
      Pageable p);

  @Query(value = """
      MATCH (u:User {userId:$userId})
      WHERE u.deletedAt IS NULL
      MATCH (u)-[c:CREATED_ORG]->(o:Organization)
      RETURN c, o AS organization
      ORDER BY c.createdAt DESC
      SKIP $skip
      LIMIT $limit
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})
          WHERE u.deletedAt IS NULL
          MATCH (u)-[c:CREATED_ORG]->(:Organization)
          RETURN count(c)
          """)
  Page<CreatedOrg> findCreatedOrgs(
      String userId,
      Pageable pageable);

  /* ========================= PACKAGE ========================= */

  @Query(value = """
      MATCH (u:User {userId:$userId})
      WHERE u.deletedAt IS NULL
      MATCH (u)-[s:SUBSCRIBED_TO]->(p:Package)
      RETURN s, p AS pkg
      ORDER BY s.start DESC
      SKIP $skip
      LIMIT $limit
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})
          WHERE u.deletedAt IS NULL
          MATCH (u)-[s:SUBSCRIBED_TO]->(:Package)
          RETURN count(s)
          """)
  Page<SubscribedTo> findSubscriptions(
      String userId,
      Pageable pageable);

  /* ========================= RESOURCE ========================= */

  @Query(value = """
      MATCH (u:User {userId:$userId})
      WHERE u.deletedAt IS NULL
      MATCH (u)-[sr:SAVED_RESOURCE]->(f:FileResource)
      WHERE f.type = $type
      RETURN sr, f AS resource
      ORDER BY sr.saveAt DESC
      SKIP $skip
      LIMIT $limit
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})
          WHERE u.deletedAt IS NULL
          MATCH (u)-[sr:SAVED_RESOURCE]->(f:FileResource)
          WHERE f.type = $type
          RETURN count(sr)
          """)
  Page<SavedResource> findSavedResourcesByType(
      String userId,
      String type,
      Pageable pageable);

  @Query("""
        MATCH (u:User {userId:$userId})
        WHERE u.deletedAt IS NULL
        MATCH (f:FileResource {fileId:$fileId})
        MERGE (u)-[r:SAVED_RESOURCE]->(f)
        ON CREATE SET r.saveAt = $now
        ON MATCH  SET r.saveAt = coalesce(r.saveAt, $now)
      """)
  void mergeSavedResource(String userId, String fileId, Instant now);

  @Query("""
        MATCH (u:User {userId:$userId})-[r:SAVED_RESOURCE]->(f:FileResource {fileId:$fileId})
        DELETE r
      """)
  void deleteSavedResource(String userId, String fileId);

  @Query("""
        MATCH (u:User {userId:$userId})
        WHERE u.deletedAt IS NULL
        MATCH (f:FileResource {fileId:$fileId})
        MERGE (u)-[r:REPORTED_RESOURCE]->(f)
        SET r.reason = $reason, r.reportedAt = $at
      """)
  void mergeReportedResource(String userId, String fileId, String reason,
                             Instant at);

  @Query("""
        MATCH (u:User {userId:$userId})-[r:REPORTED_RESOURCE]->(f:FileResource {fileId:$fileId})
        DELETE r
      """)
  void deleteReportedResource(String userId, String fileId);

  @Query("""
      MATCH (me:User {userId:$me}), (target:User {userId:$target})
      RETURN EXISTS( (me)-[:FOLLOWS]->(target) )
      """)
  boolean existsFollow(String me, String target);

  @Query("""
      MATCH (a:User {userId:$me}), (b:User {userId:$target})
      RETURN EXISTS( (a)-[:BLOCKS]->(b) ) OR EXISTS( (b)-[:BLOCKS]->(a) )
      """)
  boolean isBlockedEitherWay(String me, String target);
}
