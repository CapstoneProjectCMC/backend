package com.codecampus.quiz.repository;

import com.codecampus.quiz.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository
        extends JpaRepository<Option, String> {
}
