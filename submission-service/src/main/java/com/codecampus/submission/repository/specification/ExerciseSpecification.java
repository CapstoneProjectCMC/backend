package com.codecampus.submission.repository.specification;

import com.codecampus.submission.constant.submission.Difficulty;
import com.codecampus.submission.entity.Exercise;
import org.springframework.data.jpa.domain.Specification;

public class ExerciseSpecification {
  public static Specification<Exercise> visibleAndActive() {
    return (root, q, cb) -> cb.and(
        cb.isTrue(root.get("active")),
        cb.isTrue(root.get("visibility"))
    );
  }

  public static Specification<Exercise> hasKeyword(String keyword) {
    return (root, q, cb) -> {
      if (keyword == null || keyword.isBlank()) {
        return null;
      }
      String like = "%" + keyword.trim().toLowerCase() + "%";
      return cb.or(
          cb.like(cb.lower(root.get("title")), like),
          cb.like(cb.lower(root.get("description")), like)
      );
    };
  }

  public static Specification<Exercise> hasDifficulty(Integer diff) {
    return (root, q, cb) ->
        diff == null ? null :
            cb.equal(root.get("difficulty"), Difficulty.values()[diff]);
  }

  // gộp lại
  public static Specification<Exercise> build(
      String keyword, Integer diff) {
    return Specification.where(visibleAndActive())
        .and(hasKeyword(keyword))
        .and(hasDifficulty(diff));
  }
}
