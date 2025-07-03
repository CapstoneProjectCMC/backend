package com.codecampus.submission.repository;

import com.codecampus.submission.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestCaseRepository
    extends JpaRepository<TestCase, String>
{
}

