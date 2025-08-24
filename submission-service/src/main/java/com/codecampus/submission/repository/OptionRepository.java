package com.codecampus.submission.repository;

import com.codecampus.submission.entity.Option;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository
    extends JpaRepository<Option, String> {

  List<Option> findByQuestionId(String questionId);

  Page<Option> findByQuestionId(
      String questionId, Pageable pageable);
}

