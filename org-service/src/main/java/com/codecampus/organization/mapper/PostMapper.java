package com.codecampus.organization.mapper;

import com.codecampus.organization.entity.OrganizationPost;
import dtos.PostSummary;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
  default PostSummary toPostSummary(OrganizationPost p) {
    if (p == null) {
      return null;
    }
    return new PostSummary(
        p.getPostId(),
        p.getTitle(),
        p.getPostType(),
        p.isPublic(),
        p.getOrgId()
    );
  }
}