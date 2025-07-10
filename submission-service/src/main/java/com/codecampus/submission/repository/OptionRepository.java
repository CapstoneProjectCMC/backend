package com.codecampus.submission.repository;

import com.codecampus.submission.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository
        extends JpaRepository<Option, String> {
    List<Option> findByQuestionId(String questionId);
}

