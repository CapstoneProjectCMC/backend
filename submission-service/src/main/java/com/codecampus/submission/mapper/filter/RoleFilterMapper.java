package com.codecampus.submission.mapper.filter;

import com.codecampus.submission.helper.AuthenticationHelper;
import org.mapstruct.AfterMapping;

public interface RoleFilterMapper<Entity, Dto> {


  @AfterMapping
  default void afterMapping(
      Entity entity,
      Dto dto) {
    if (entity == null || AuthenticationHelper.getMyRoles() == null) {
      filterForUser(dto);
    }

    if (AuthenticationHelper
        .getMyRoles()
        .stream()
        .anyMatch(role -> role.equals("USER"))) {
      filterForUser(dto);
    } else if (AuthenticationHelper
        .getMyRoles()
        .stream()
        .anyMatch(role -> role.equals("TEACHER"))) {
      filterForTeacher(dto);
    }
  }

  default void filterForUser(Dto dto) {
  }

  default void filterForTeacher(Dto dto) {
  }
}
