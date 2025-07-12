package com.codecampus.submission.repository;

import com.codecampus.submission.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository
        extends JpaRepository<Question, String> {

    List<Question> findByQuizDetailId(String quizDetailId);

    Page<Question> findByQuizDetailId(
            String quizDetailId, Pageable pageable);

    default List<Question> getFirstNQuestions(
            String exerciseId, int n, Sort sort) {
        return findByQuizDetailId(
                exerciseId, PageRequest.of(0, n, sort)
        ).getContent();
    }
}

