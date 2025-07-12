package com.codecampus.coding.repository;

import com.codecampus.coding.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestCaseRepository
        extends JpaRepository<TestCase, String> {
}

