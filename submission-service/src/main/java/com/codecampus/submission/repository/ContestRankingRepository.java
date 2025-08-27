package com.codecampus.submission.repository;

import com.codecampus.submission.entity.ContestRanking;
import com.codecampus.submission.entity.data.ContestRankId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestRankingRepository
    extends JpaRepository<ContestRanking, ContestRankId> {

  List<ContestRanking> findByContestIdOrderByScoreDescRankAsc(
      String contestId);

  Optional<ContestRanking> findById(ContestRankId id);
}