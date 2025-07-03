package com.codecampus.submission.repository;

import com.codecampus.submission.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository
    extends JpaRepository<Option, String>
{
}

