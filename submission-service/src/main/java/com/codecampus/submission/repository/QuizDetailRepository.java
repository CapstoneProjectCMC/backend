package com.codecampus.submission.repository;

import com.codecampus.submission.entity.QuizDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizDetailRepository
    extends JpaRepository<QuizDetail, String> {
}
