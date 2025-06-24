package com.codecampus.submission.repository.quiz;

import com.codecampus.submission.entity.QuizDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository
    extends JpaRepository<QuizDetail, String>
{
}
