package com.codecampus.post.mapper;


import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import com.codecampus.post.dto.request.PostRequestDto;
import com.codecampus.post.entity.Post;
import dtos.PostSummary;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {
  PostRequestDto toPostRequestDtoFromPost(
      Post post);

  List<PostRequestDto> toPostRequestDtoListFromPostList(
      List<Post> posts);

  @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
  Post toPostFromPostRequestDto(
      PostRequestDto postRequestDto);

  @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
  void updatePostRequestDtoToPost(
      PostRequestDto postRequestDto,
      @MappingTarget Post post);

  PostSummary toPostSummaryFromPost(Post p);
}
