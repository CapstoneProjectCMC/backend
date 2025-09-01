package com.codecampus.search.mapper;

import com.codecampus.search.entity.PostDocument;
import events.post.data.PostPayload;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

  @Mapping(target = "id", source = "id")
  @Mapping(target = "allowUserIds", ignore = true)
  @Mapping(target = "excludeUserIds", ignore = true)
  PostDocument toPostDocumentFromPostPayload(
      PostPayload payload);
}