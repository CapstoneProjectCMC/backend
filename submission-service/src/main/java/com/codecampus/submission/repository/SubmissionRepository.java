package com.codecampus.submission.repository;

import com.codecampus.submission.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository
        extends JpaRepository<Submission, String> {
    List<Submission> findByUserId(
            String userId);

    Optional<Submission> findByExerciseIdAndUserId(
            String exerciseId,
            String userId);

    @Query("""
            select s
                from Submission s
                join fetch s.exercise e
            where s.userId = :studentId
                and e.exerciseType = com.codecampus.submission.constant.submission.ExerciseType.QUIZ
            """)
    List<Submission> findQuizSubmissionsByStudent(String studentId);

    @Query("""
                 select s
                   from Submission s
                   join s.exercise e
                   join e.contests c
                  where c.id = :contestId
                    and s.userId = :studentId
                    and e.exerciseType = com.codecampus.submission.constant.submission.ExerciseType.QUIZ
            """)
    List<Submission> findQuizSubmissionByContestAndStudent(
            String contestId,
            String studentId);

    @Query("""
            select s
              from Submission s
              join s.exercise e
             where s.userId = :studentId
               and e.id in :exerciseIds
             order by s.score desc, s.timeTakenSeconds asc nulls last
            """)
    List<Submission> findBestPerQuizSubmissionExercises(
            String studentId,
            Collection<String> exerciseIds);

    @Query("""
              select max(s.score)
                from Submission s
               where s.exercise.id = :exerciseId
                 and s.userId = :studentId
            """)
    Integer findBestScoreFromExercise(String exerciseId, String studentId);
}

