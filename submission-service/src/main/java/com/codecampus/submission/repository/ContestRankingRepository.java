package com.codecampus.submission.repository;

import com.codecampus.submission.entity.ContestRanking;
import com.codecampus.submission.entity.data.ContestRankId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContestRankingRepository
        extends JpaRepository<ContestRanking, ContestRankId> {

    List<ContestRanking> findByContestIdOrderByScoreDescRankAsc(
            String contestId);
}