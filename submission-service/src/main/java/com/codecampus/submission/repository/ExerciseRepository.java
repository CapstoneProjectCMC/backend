package com.codecampus.submission.repository;

import com.codecampus.submission.entity.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository
    extends JpaRepository<Exercise, String>,
    JpaSpecificationExecutor<Exercise> {
  Page<Exercise> findAll(
      Specification<Exercise> spec,
      Pageable pageable
  );
}