package com.codecampus.submission.repository.coding;

import com.codecampus.submission.entity.CodingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodingRepository
    extends JpaRepository<CodingDetail, String>
{
}
