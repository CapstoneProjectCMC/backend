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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
    Optional<UserProfile> findActiveByUserId(
            String userId);

    boolean existsByUserId(String userId);

    // Exercise
    @Query(value = """
            MATCH (u:User {userId:$userId})-[completed:COMPLETED_EXERCISE]->(e:Exercise)
            RETURN completed, e
            ORDER BY completed.completedAt DESC
            """,
            countQuery = """
                    MATCH (u:User {userId:$userId})-[completed:COMPLETED_EXERCISE]->(:Exercise)
                    RETURN count(completed)
                    """)
    Page<CompletedExercise> findCompletedExercises(
            String userId, Pageable pageable);


    @Query(value = """
            MATCH (u:User {userId:$userId})-[saved:SAVED_EXERCISE]->(e:Exercise)
            RETURN saved, e
            ORDER BY saved.saveAt DESC
            """,
            countQuery = """
                        MATCH (u:User {userId:$userId})-[saved:SAVED_EXERCISE]->(:Exercise)
                        RETURN count(saved)
                    """
    )
    Page<SavedExercise> findSavedExercises(
            String userId, Pageable pageable);

    @Query(value = """
            MATCH (u:User {userId:$userId})-[created:CREATED_EXERCISE]->(e:Exercise)
            RETURN created, e
            ORDER BY created.id DESC
            """,
            countQuery = """
                        MATCH (u:User {userId:$userId})-[created:CREATED_EXERCISE]->(:Exercise)
                        RETURN count(created)
                    """)
    Page<CreatedExercise> findCreatedExercises(
            String userId, Pageable pageable);

    @Query(value = """
            MATCH (u:User {userId:$userId})-[cs:CONTEST_STATUS]->(c:Contest)
            RETURN cs, c
            ORDER BY cs.updatedAt DESC
            """,
            countQuery = """
                        MATCH (u:User {userId:$userId})-[cs:CONTEST_STATUS]->(:Contest)
                        RETURN count(cs)
                    """
    )
    Page<ContestStatus> findContestStatuses(
            String userId, Pageable pageable);

    // Post
    @Query(value = """
            MATCH (u:User {userId:$userId})-[sp:SAVED_POST]->(p:Post)
            RETURN sp, p
            ORDER BY sp.saveAt DESC
            """,
            countQuery = """
                        MATCH (u:User {userId:$userId})-[sp:SAVED_POST]->(:Post)
                        RETURN count(sp)
                    """)
    Page<SavedPost> findSavedPosts(
            String userId, Pageable pageable);

    @Query(value = """
            MATCH (u:User {userId:$userId})-[r:REACTION]->(p:Post)
            RETURN r, p
            ORDER BY r.at DESC
            """,
            countQuery = """
                        MATCH (u:User {userId:$userId})-[r:REACTION]->(:Post)
                        RETURN count(r)
                    """
    )
    Page<Reaction> findReactions(String userId, Pageable pageable);

    @Query(value = """
            MATCH (u:User {userId:$userId})-[rp:REPORTED_POST]->(p:Post)
            RETURN rp, p
            ORDER BY rp.reportedAt DESC
            """,
            countQuery = """
                        MATCH (u:User {userId:$userId})-[rp:REPORTED_POST]->(:Post)
                        RETURN count(rp)
                    """
    )
    Page<ReportedPost> findReportedPosts(String userId, Pageable pageable);

    // Activity Time
    @Query(value = """
            MATCH (u:User {userId:$userId})-[:HAS_ACTIVITY]->(a:ActivityWeek)
            RETURN a
            ORDER BY a.weekStart DESC
            """,
            countQuery = """
                        MATCH (u:User {userId:$userId})-[:HAS_ACTIVITY]->(a:ActivityWeek)
                        RETURN count(a)
                    """
    )
    Page<ActivityWeek> findActivityWeek(
            String userId, Pageable pageable);

    // Follow / Block
    @Query(value = """
            MATCH (me:User {userId:$userId})-[f:FOLLOWS]->(target:User)
            RETURN f, target
            ORDER BY f.since DESC
            """,
            countQuery = """
                        MATCH (me:User {userId:$userId})-[f:FOLLOWS]->(:User)
                        RETURN count(f)
                    """
    )
    Page<Follows> findFollowings(String userId, Pageable pageable);

    @Query(value = """
            MATCH (src:User)-[f:FOLLOWS]->(me:User {userId:$userId})
            RETURN f, src
            ORDER BY f.since DESC
            """,
            countQuery = """
                        MATCH (:User)-[f:FOLLOWS]->(me:User {userId:$userId})
                        RETURN count(f)
                    """
    )
    Page<Follows> findFollowers(String userId, Pageable pageable);

    @Query(value = """
            MATCH (me:User {userId:$userId})-[b:BLOCKS]->(target:User)
            RETURN b, target
            ORDER BY b.since DESC
            """,
            countQuery = """
                        MATCH (me:User {userId:$userId})-[b:BLOCKS]->(:User)
                        RETURN count(b)
                    """
    )
    Page<Blocks> findBlocked(String userId, Pageable pageable);

    // Org
    @Query(value = """
            MATCH (u:User {userId:$userId})-[m:MEMBER_ORG]->(o:Organization)
            RETURN m, o
            ORDER BY m.joinAt DESC
            """,
            countQuery = """
                        MATCH (u:User {userId:$userId})-[m:MEMBER_ORG]->(:Organization)
                        RETURN count(m)
                    """
    )
    Page<MemberOrg> findMemberOrgs(String userId, Pageable pageable);

    @Query(value = """
            MATCH (u:User {userId:$userId})-[m:MEMBER_ORG]->(o:Organization)
            WHERE m.memberRole = $role
            RETURN m, o
            ORDER BY m.joinAt DESC
            """,
            countQuery = """
                        MATCH (u:User {userId:$userId})-[m:MEMBER_ORG]->(:Organization)
                        WHERE m.memberRole = $role
                        RETURN count(m)
                    """
    )
    Page<MemberOrg> findMemberOrgsByRole(String userId, String role,
                                         Pageable p);

    @Query(value = """
            MATCH (u:User {userId:$userId})-[c:CREATED_ORG]->(o:Organization)
            RETURN c, o
            ORDER BY c.createdAt DESC
            """,
            countQuery = """
                        MATCH (u:User {userId:$userId})-[c:CREATED_ORG]->(:Organization)
                        RETURN count(c)
                    """
    )
    Page<CreatedOrg> findCreatedOrgs(
            String userId, Pageable pageable);

    // Package
    @Query(value = """
            MATCH (u:User {userId:$userId})-[s:SUBSCRIBED_TO]->(p:Package)
            RETURN s, p
            ORDER BY s.start DESC
            """,
            countQuery = """
                        MATCH (u:User {userId:$userId})-[s:SUBSCRIBED_TO]->(:Package)
                        RETURN count(s)
                    """
    )
    Page<SubscribedTo> findSubscriptions(
            String userId, Pageable pageable);

    // Resource
    @Query(
            value = """
                    MATCH (u:User {userId:$userId})-[sr:SAVED_RESOURCE]->(f:FileResource)
                    WHERE f.type = $type
                    RETURN sr, f
                    ORDER BY sr.saveAt DESC
                    """,
            countQuery = """
                        MATCH (u:User {userId:$userId})-[sr:SAVED_RESOURCE]->(f:FileResource)
                        WHERE f.type = $type
                        RETURN count(sr)
                    """
    )
    Page<SavedResource> findSavedResourcesByType(
            String userId, String type, Pageable pageable);
}
