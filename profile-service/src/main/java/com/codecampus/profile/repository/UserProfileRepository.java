package com.codecampus.profile.repository;

import com.codecampus.profile.entity.ActivityWeek;
import com.codecampus.profile.entity.UserProfile;
import com.codecampus.profile.entity.properties.exercise.CompletedExercise;
import com.codecampus.profile.entity.properties.exercise.CreatedExercise;
import com.codecampus.profile.entity.properties.exercise.SavedExercise;
import com.codecampus.profile.entity.properties.post.SavedPost;
import com.codecampus.profile.entity.properties.social.Blocks;
import com.codecampus.profile.entity.properties.social.Follows;
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
}
