package com.codecampus.coding.repository;

import com.codecampus.coding.entity.CodeSubmission;
import com.codecampus.coding.entity.data.CodeSubmissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CodeSubmissionRepository
        extends JpaRepository<CodeSubmission, CodeSubmissionId> {
    List<CodeSubmission> findByIdSubmissionId(
            String submissionId);

    @Query("""
                SELECT COUNT(*) FROM CodeSubmission cs
                WHERE cs.id.submissionId = :submissionId
                  AND cs.passed = true
            """)
    int countPassed(@Param("submissionId") String submissionId);
}
