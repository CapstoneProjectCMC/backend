package com.codecampus.submission.repository;

import com.codecampus.submission.entity.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContestRepository
        extends JpaRepository<Contest, String> {

    @Query("""
                select c from Contest c
                  join c.exercises e
                  join Assignment a on a.exercise = e
                 where a.studentId = :studentId
            """)
    List<Contest> findAllContestsForStudent(
            String studentId);

    @Query("""
                select c from Contest c
                  join c.exercises e
                 where e.id = :exerciseId
            """)
    List<Contest> findAllContestsByExerciseId(
            String exerciseId);
}
