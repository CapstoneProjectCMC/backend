package com.codecampus.identity.entity.audit;

import java.time.Instant;
import java.util.Optional;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserRevisionListener implements RevisionListener
{
  @Override
  public void newRevision(Object revisionEntity)
  {
    RevisionInfo revisionInfo = (RevisionInfo) revisionEntity;
    revisionInfo.setTimestamp(Instant.now().toEpochMilli());
    revisionInfo.setOperator(Optional.ofNullable(SecurityContextHolder
            .getContext().getAuthentication())
        .map(Authentication::getName)
        .orElse("system"));
  }
}