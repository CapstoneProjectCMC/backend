package com.codecampus.coding.repository;

import com.codecampus.coding.entity.CodeSubmissionResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeSubmissionResultRepository
        extends JpaRepository<CodeSubmissionResult, String> {
}
