package com.codecampus.submission.mapper;

import com.codecampus.submission.dto.response.exercise.AssignmentResponse;
import com.codecampus.submission.entity.Assignment;
import com.codecampus.submission.mapper.filter.RoleFilterMapper;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    uses = {ExerciseMapper.class}
)
public interface AssignmentMapper
    extends RoleFilterMapper<Assignment, AssignmentResponse> {
  AssignmentResponse toAssignmentResponse(Assignment assignment);


  @Override
  default void filterForUser(AssignmentResponse assignmentResponse) {
    RoleFilterMapper.super.filterForUser(assignmentResponse);
  }

  @Override
  default void filterForTeacher(AssignmentResponse assignmentResponse) {
    RoleFilterMapper.super.filterForTeacher(assignmentResponse);
  }
}
