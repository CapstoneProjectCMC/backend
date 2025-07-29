package com.codecampus.submission.repository;

import com.codecampus.submission.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository
        extends JpaRepository<Assignment, String> {
    Optional<Assignment> findByExerciseIdAndStudentId(
            String exerciseId,
            String studentId);

    List<Assignment> findByStudentId(String studentId);

    boolean existsByExerciseIdAndStudentIdAndCompletedTrue(
            String exerciseId,
            String studentId);
}
