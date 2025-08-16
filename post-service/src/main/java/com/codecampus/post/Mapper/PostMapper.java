package com.codecampus.post.Mapper;


import com.codecampus.post.dto.request.PostRequestDto;
import com.codecampus.post.entity.Post;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostRequestDto toDto(Post post);
    List<PostRequestDto> toDtoList(List<Post> posts);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Post toEntity(PostRequestDto postRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(PostRequestDto postRequestDto, @MappingTarget Post post);
}
