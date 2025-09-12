package com.codecampus.organization.mapper;

import com.codecampus.organization.dto.request.UpdateBlockRequest;
import com.codecampus.organization.entity.OrganizationBlock;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BlockMapper {

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void patchUpdateOrganizationBlockFromUpdateBlockRequest(
      UpdateBlockRequest request,
      @MappingTarget OrganizationBlock organizationBlock);
}
