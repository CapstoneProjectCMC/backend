package com.codecampus.submission.mapper;

import com.codecampus.submission.dto.response.exercise.SubmissionResponse;
import com.codecampus.submission.entity.Submission;
import com.codecampus.submission.mapper.filter.RoleFilterMapper;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {ExerciseMapper.class}
)
public interface SubmissionMapper
    extends RoleFilterMapper<Submission, SubmissionResponse> {

  SubmissionResponse toSubmissionResponse(Submission submission);

  @Override
  default void filterForUser(SubmissionResponse response) {
    RoleFilterMapper.super.filterForUser(response);
    response.setUserId(null);
  }

  @Override
  default void filterForTeacher(SubmissionResponse response) {
    RoleFilterMapper.super.filterForTeacher(response);
  }
}
