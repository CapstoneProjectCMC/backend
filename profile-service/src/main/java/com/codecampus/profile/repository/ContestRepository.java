package com.codecampus.profile.repository;

import com.codecampus.profile.entity.Contest;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestRepository
    extends Neo4jRepository<Contest, String> {
  Optional<Contest> findByContestId(String contestId);

  void deleteByContestId(String contestId);

  // Tạo nếu chưa có, luôn trả về đúng 1 node
  @Query("""
        MERGE (c:Contest {contestId: $contestId})
        RETURN c
      """)
  Contest mergeByContestId(String contestId);

  // Upsert đầy đủ field khi có payload
  @Query("""
        MERGE (c:Contest {contestId: $contestId})
        ON CREATE SET
          c.title  = $title,
          c.startAt = $startAt,
          c.endAt   = $endAt
        ON MATCH SET
          c.title   = coalesce($title,  c.title),
          c.startAt = coalesce($startAt,c.startAt),
          c.endAt   = coalesce($endAt,  c.endAt)
        RETURN c
      """)
  Contest upsertContest(
      String contestId,
      String title,
      Instant startAt,
      Instant endAt);
}
