package com.codecampus.submission.repository;

import com.codecampus.submission.entity.Contest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestRepository
    extends JpaRepository<Contest, String> {
}

