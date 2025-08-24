package com.codecampus.coding.repository;

import com.codecampus.coding.entity.CodeSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeSubmissionRepository
    extends JpaRepository<CodeSubmission, String> {
}
