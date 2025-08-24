package com.codecampus.submission.repository;

import com.codecampus.submission.entity.TestCase;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestCaseRepository
    extends JpaRepository<TestCase, String> {

  List<TestCase> findByCodingDetailExerciseId(
      String exerciseId);

  Page<TestCase> findByCodingDetailExerciseId(
      String exerciseId,
      Pageable pageable);
}

