package com.codecampus.submission.repository;

import com.codecampus.submission.constant.submission.SubmissionStatus;
import com.codecampus.submission.entity.Submission;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository
    extends JpaRepository<Submission, String> {

  List<Submission> findByUserIdOrderBySubmittedAtDesc(
      String userId);

  Page<Submission> findByUserId(
      String userId,
      Pageable pageable);

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
  Page<Submission> findQuizSubmissionsByStudent(
      String studentId,
      Pageable pageable);

  @Query("""
      select s
          from Submission s
          join fetch s.exercise e
      where s.userId = :studentId
          and e.exerciseType = com.codecampus.submission.constant.submission.ExerciseType.QUIZ
      """)
  List<Submission> findQuizSubmissionsByStudent(
      String studentId);

  @Query("""
      select s
          from Submission s
          join fetch s.exercise e
      where s.userId = :studentId
          and e.exerciseType = com.codecampus.submission.constant.submission.ExerciseType.CODING
      """)
  Page<Submission> findCodingSubmissionsByStudent(
      String studentId,
      Pageable pageable);

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
             join e.contests c
            where c.id = :contestId
              and s.userId = :studentId
              and e.exerciseType = com.codecampus.submission.constant.submission.ExerciseType.CODING
      """)
  List<Submission> findCodingSubmissionByContestAndStudent(
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
  List<Submission> findBestPerSubmissionExercises(
      String studentId,
      Collection<String> exerciseIds);

  @Query("""
        select max(s.score)
          from Submission s
         where s.exercise.id = :exerciseId
           and s.userId = :studentId
      """)
  Integer findBestScoreFromExercise(
      String exerciseId, String studentId);

  Optional<Submission> findFirstByExerciseIdAndUserIdAndStatusOrderBySubmittedAtAsc(
      String exerciseId,
      String userId,
      SubmissionStatus status
  );

  Optional<Submission> findFirstByExerciseIdAndUserIdOrderBySubmittedAtAsc(
      String exerciseId,
      String userId
  );

  Integer countByExerciseIdAndUserId(String exerciseId, String userId);

  List<Submission> findByUserIdAndExerciseId(String userId, String exerciseId);
}

