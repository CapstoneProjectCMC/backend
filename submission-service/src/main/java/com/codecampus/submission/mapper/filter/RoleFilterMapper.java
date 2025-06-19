package com.codecampus.submission.mapper.filter;

import com.codecampus.submission.utils.SecurityUtils;
import org.mapstruct.AfterMapping;

public interface RoleFilterMapper<Entity, Dto> {


  @AfterMapping
  default void afterMapping(
      Entity entity,
      Dto dto) {
    if (entity == null || SecurityUtils.getMyRoles() == null) {
      filterForUser(dto);
    }

    if (SecurityUtils
        .getMyRoles()
        .stream()
        .anyMatch(role -> role.equals("USER"))) {
      filterForUser(dto);
    } else if (SecurityUtils
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
