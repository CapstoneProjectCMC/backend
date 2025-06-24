package com.codecampus.submission.repository;

import com.codecampus.submission.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository
    extends JpaRepository<Assignment, String> {
}
