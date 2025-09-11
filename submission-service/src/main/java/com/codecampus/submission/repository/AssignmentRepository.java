package com.codecampus.submission.repository;

import com.codecampus.submission.entity.Assignment;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository
    extends JpaRepository<Assignment, String> {
  Optional<Assignment> findByExerciseIdAndStudentId(
      String exerciseId,
      String studentId);

  List<Assignment> findByStudentId(String studentId);

  Page<Assignment> findByStudentId(String studentId, Pageable pageable);

  Page<Assignment> findByExerciseId(String exerciseId, Pageable pageable);

  List<Assignment> findByExerciseIdAndStudentIdIn(
      String exerciseId,
      Collection<String> studentIds);

  Page<Assignment> findByExerciseIdAndCompleted(
      String exerciseId,
      boolean completed,
      Pageable pageable);

  boolean existsByExerciseIdAndStudentIdAndCompletedTrue(
      String exerciseId,
      String studentId);

  List<Assignment> findByStudentIdAndCompleted(
      String studentId,
      boolean completed);

  @Query("""
      select a.exercise.id, count(a)
        from Assignment a
       where a.exercise.id in :exerciseIds
       group by a.exercise.id
      """)
  List<Object[]> countAssignmentsByExerciseIds(Collection<String> exerciseIds);

  @Query("""
      select a.exercise.id, count(a)
        from Assignment a
       where a.exercise.id in :exerciseIds
         and a.completed = true
       group by a.exercise.id
      """)
  List<Object[]> countCompletedAssignmentsByExerciseIds(
      Collection<String> exerciseIds);
}
