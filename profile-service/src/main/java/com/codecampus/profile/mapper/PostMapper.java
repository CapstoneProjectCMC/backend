package com.codecampus.profile.mapper;


import com.codecampus.profile.entity.Post;
import dtos.PostSummary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
  @Mapping(target = "postId", source = "id")
  Post toPostFromPostSummary(PostSummary postSummary);
}