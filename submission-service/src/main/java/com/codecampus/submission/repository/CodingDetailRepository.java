package com.codecampus.submission.repository;

import com.codecampus.submission.entity.CodingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodingDetailRepository
        extends JpaRepository<CodingDetail, String> {
}

