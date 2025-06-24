package com.codecampus.submission.repository.quiz;

import com.codecampus.submission.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository
    extends JpaRepository<Option, String>
{
}
