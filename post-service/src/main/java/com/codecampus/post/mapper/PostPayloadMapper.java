package com.codecampus.post.mapper;

import com.codecampus.post.entity.Post;
import events.post.data.PostPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostPayloadMapper {
  @Mapping(target = "id", source = "postId")
  PostPayload toPostPayloadFromPost(Post p);
}