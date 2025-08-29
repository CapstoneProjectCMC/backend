package com.codecampus.organization.mapper;

import com.codecampus.organization.dto.response.BlockResponse;
import com.codecampus.organization.entity.OrganizationBlock;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationBlockMapper {

  default BlockResponse toBlockResponseFromOrganizationBlock(
      OrganizationBlock b) {
    return BlockResponse.builder()
        .id(b.getId())
        .orgId(b.getOrgId())
        .name(b.getName())
        .code(b.getCode())
        .description(b.getDescription())
        .createdAt(b.getCreatedAt())
        .updatedAt(b.getUpdatedAt())
        .build();
  }
}
