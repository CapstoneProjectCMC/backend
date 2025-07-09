package com.codecampus.quiz.repository;

import com.codecampus.quiz.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository
        extends JpaRepository<Assignment, String> {
    boolean existsByExerciseIdAndStudentId(
            String exerciseId, String studentId);
}