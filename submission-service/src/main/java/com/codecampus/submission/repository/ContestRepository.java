package com.codecampus.submission.repository;

import com.codecampus.submission.entity.Contest;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContestRepository
    extends JpaRepository<Contest, String> {
  // Lấy các contest chưa kết thúc
  @Query("""
               select c from Contest c
               where c.endTime > :now
               order by c.startTime desc
      """)
  Page<Contest> findIncomingContests(
      Instant now,
      Pageable pageable
  );
}

