package com.codecampus.submission.repository;

import com.codecampus.submission.entity.TestCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCaseRepository
        extends JpaRepository<TestCase, String> {

    List<TestCase> findByCodingDetailExerciseId(
            String exerciseId);

    Page<TestCase> findByCodingDetailExerciseId(
            String exerciseId,
            Pageable pageable);
}

