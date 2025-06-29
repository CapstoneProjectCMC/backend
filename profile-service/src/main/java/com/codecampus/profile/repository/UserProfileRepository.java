package com.codecampus.profile.repository;

import com.codecampus.profile.entity.ActivityWeek;
import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.entity.properties.contest.ContestStatus;
import com.codecampus.profile.entity.properties.exercise.CompletedExercise;
import com.codecampus.profile.entity.properties.exercise.CreatedExercise;
import com.codecampus.profile.entity.properties.exercise.SavedExercise;
import com.codecampus.profile.entity.properties.organization.CreatedOrg;
import com.codecampus.profile.entity.properties.organization.EnrolledClass;
import com.codecampus.profile.entity.properties.organization.ManagesClass;
import com.codecampus.profile.entity.properties.organization.MemberOrg;
import com.codecampus.profile.entity.properties.post.Reaction;
import com.codecampus.profile.entity.properties.post.ReportedPost;
import com.codecampus.profile.entity.properties.post.SavedPost;
import com.codecampus.profile.entity.properties.resource.SavedResource;
import com.codecampus.profile.entity.properties.social.Blocks;
import com.codecampus.profile.entity.properties.social.Follows;
import com.codecampus.profile.entity.properties.subcribe.SubscribedTo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository
    extends Neo4jRepository<UserProfile, String>
{
  Optional<UserProfile> findByUserId(String userId);

  boolean existsByUserId(String userId);

  // Exercise
  @Query(value = """
      MATCH (u:User {userid:$userId})-[:COMPLETED_EXERCISE]->(completed:CompletedExercise)-[:TARGET_NODE]->(e)
      RETURN completed ORDER BY completed.completedAt DESC
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})-[:COMPLETED_EXERCISE]->(completed:CompletedExercise)
          RETURN count(completed)
          """)
  Page<CompletedExercise> findCompletedExercises(
      String userId, Pageable pageable);


  @Query(value = """
      MATCH (u:User {userId:$userId})-[:SAVED_EXERCISE]->(saved:SavedExercise)-[:TARGET_NODE]->(e)
      RETURN saved ORDER BY saved.saveAt DESC
      """,
      countQuery = """
            MATCH (u:User {userId:$userId})-[:SAVED_EXERCISE]->(saved:SavedExercise)
            RETURN count(saved)
          """)
  Page<SavedExercise> findSavedExercises(
      String userId, Pageable pageable);

  @Query(value = """
      MATCH (u:User {userId:$userId})-[:CREATED_EXERCISE]->(created:CreatedExercise)-[:TARGET_NODE]->(e)
      RETURN created
      """,
      countQuery = """
            MATCH (u:User {userId:$userId})-[:CREATED_EXERCISE]->(created:CreatedExercise)
            RETURN count(created)
          """)
  Page<CreatedExercise> findCreatedExercises(
      String userId, Pageable pageable);

  @Query(
      value = """
            MATCH (u:User {userId:$userId})-[:CONTEST_STATUS]->(cs)
            RETURN cs
          """,
      countQuery = """
            MATCH (u:User {userId:$userId})-[:CONTEST_STATUS]->(cs)
            RETURN count(cs)
          """
  )
  Page<ContestStatus> findContestStatuses(
      String userId, Pageable pageable);

  // Post
  @Query(value = """
      MATCH (u:User {userId:$userId})-[:SAVED_POST]->(saved:SavedPost)-[:TARGET_NODE]->(e)
      RETURN saved ORDER BY saved.saveAt DESC
      """,
      countQuery = """
            MATCH (u:User {userId:$userId})-[:SAVED_POST]->(saved:SavedPost)
            RETURN count(saved)
          """)
  Page<SavedPost> findSavedPosts(
      String userId, Pageable pageable);

  @Query(value = """
      MATCH (u:User {userId:$userId})-[r:REACTION]->(p:Post)
      RETURN r, p ORDER BY r.at DESC
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})-[r:REACTION]->(:Post)
          RETURN count(r)
          """)
  Page<Reaction> findReactions(String userId, Pageable pageable);

  @Query(value = """
      MATCH (u:User {userId:$userId})-[r:REPORTED_POST]->(p:Post)
      RETURN r, p ORDER BY r.at DESC
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})-[r:REPORTED_POST]->(:Post)
          RETURN count(r)
          """)
  Page<ReportedPost> findReportedPosts(String userId, Pageable pageable);

  // Activity Time
  @Query(value = """
      MATCH (u:User {userId:$userId})-[:HAS_ACTIVITY]->(activity:ActivityWeek)-[:TARGET_NODE]->(e)
      RETURN activity ORDER BY activity.weekStart DESC
      """,
      countQuery = """
            MATCH (u:User {userId:$userId})-[:HAS_ACTIVITY]->(activity:ActivityWeek)
            RETURN count(activity)
          """)
  Page<ActivityWeek> findActivityWeek(
      String userId, Pageable pageable);

  // Follow / Block
  @Query(value = """
      MATCH (me:User {userId:$userId})-[f:FOLLOWS]->(target:User)
      WITH f, target
      ORDER BY f.since DESC
      RETURN f, target
      """,
      countQuery = """
          MATCH (me:User {userId:$userId})-[f:FOLLOWS]->(:User)
          RETURN count(f)
          """)
  Page<Follows> findFollowings(String userId, Pageable pageable);

  @Query(value = """
      MATCH (src:User)-[f:FOLLOWS]->(me:User {userId:$userId})
      WITH f, src, me
      ORDER BY f.since DESC
      RETURN f, me
      """,
      countQuery = """
          MATCH (:User)-[f:FOLLOWS]->(me:User {userId:$userId})
          RETURN count(f)
          """)
  Page<Follows> findFollowers(String userId, Pageable pageable);

  @Query(value = """
      MATCH (me:User {userId:$userId})-[b:BLOCKS]->(target:User)
      WITH b, target
      ORDER BY b.since DESC
      RETURN b, target
      """,
      countQuery = """
          MATCH (me:User {userId:$userId})-[b:BLOCKS]->(:User)
          RETURN count(b)
          """)
  Page<Blocks> findBlocked(String userId, Pageable pageable);

  // Class
  @Query(value = """
      MATCH (u:User {userId:$userId})-[mc:MANAGES_CLASS]->(c:Class)
      RETURN mc, c ORDER BY mc.enrolledAt DESC
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})-[mc:MANAGES_CLASS]->(:Class)
          RETURN count(mc)
          """)
  Page<ManagesClass> findManagedClasses(
      String userId, Pageable pageable);

  @Query(value = """
      MATCH (u:User {userId:$userId})-[e:ENROLLED_IN]->(c:Class)
      RETURN e, c ORDER BY c.name
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})-[e:ENROLLED_IN]->(:Class)
          RETURN count(e)
          """)
  Page<EnrolledClass> findEnrolledClasses(
      String userId, Pageable pageable);

  // Org
  @Query(value = """
      MATCH (u:User {userId:$userId})-[m:MEMBER_ORG]->(o:Organization)
      RETURN m, o ORDER BY m.joinAt DESC
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})-[m:MEMBER_ORG]->(:Organization)
          RETURN count(m)
          """)
  Page<MemberOrg> findMemberOrgs(String userId, Pageable pageable);

  @Query(value = """
      MATCH (u:User {userId:$userId})-[c:CREATED_ORG]->(o:Organization)
      RETURN c, o ORDER BY c.createdAt DESC
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})-[c:CREATED_ORG]->(:Organization)
          RETURN count(c)
          """)
  Page<CreatedOrg> findCreatedOrgs(
      String userId, Pageable pageable);

  // Package
  @Query(value = """
      MATCH (u:User {userId:$userId})-[s:SUBSCRIBED_TO]->(p:Package)
      RETURN s, p ORDER BY s.start DESC
      """,
      countQuery = """
          MATCH (u:User {userId:$userId})-[s:SUBSCRIBED_TO]->(:Package)
          RETURN count(s)
          """)
  Page<SubscribedTo> findSubscriptions(
      String userId, Pageable pageable);

  // Resource
  @Query(
      value = """
            MATCH (u:User {userId:$userId})-[:SAVED_RESOURCE]->(sr)-[:RESOURCE]->(f)
            WHERE f.type = $type
            RETURN sr
          """,
      countQuery = """
            MATCH (u:User {userId:$userId})-[:SAVED_RESOURCE]->(sr)-[:RESOURCE]->(f)
            WHERE f.type = $type
            RETURN count(sr)
          """
  )
  Page<SavedResource> findSavedResourcesByType(
      String userId, String type, Pageable pageable);
}
