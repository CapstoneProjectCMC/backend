package com.codecampus.submission.repository.quiz;

import com.codecampus.submission.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository
    extends JpaRepository<Question, String>
{
}
