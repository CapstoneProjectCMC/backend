package com.codecampus.coding.repository;

import com.codecampus.coding.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository
    extends JpaRepository<Assignment, String> {
  boolean existsByExerciseIdAndStudentId(
      String exerciseId, String studentId);
}