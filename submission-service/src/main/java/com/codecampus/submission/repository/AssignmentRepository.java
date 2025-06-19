package com.codecampus.submission.repository;

import com.codecampus.submission.entity.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository
    extends JpaRepository<Assignment, String> {
  Page<Assignment> findByStudentId(
      String studentId,
      Pageable pageable
  );

  boolean existsByStudentIdAndExerciseId(
      String studentId,
      String exerciseId
  );
}
