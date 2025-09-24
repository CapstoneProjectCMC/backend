package com.codecampus.organization.mapper;

import com.codecampus.organization.dto.request.UpdateOrganizationForm;
import com.codecampus.organization.dto.response.OrganizationResponse;
import com.codecampus.organization.entity.Organization;
import events.org.data.OrganizationPayload;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "logoUrl", ignore = true)
  @Mapping(target = "name", ignore = true)
  void patchUpdateOrganizationFromUpdateOrganizationForm(
      UpdateOrganizationForm request,
      @MappingTarget Organization organization);

  default OrganizationPayload toOrganizationPayloadFromOrganization(
      Organization organization) {
    if (organization == null) {
      return null;
    }
    return OrganizationPayload.builder()
        .name(organization.getName())
        .description(organization.getDescription())
        .ownerId(organization.getOwnerId())
        .logoUrl(organization.getLogoUrl())
        .email(organization.getEmail())
        .phone(organization.getPhone())
        .address(organization.getAddress())
        .status(organization.getStatus())
        .updatedAt(organization.getUpdatedAt())
        .build();
  }

  default OrganizationResponse toOrganizationResponseFromOrganization(
      Organization o) {
    return OrganizationResponse.builder()
        .id(o.getId())
        .name(o.getName())
        .description(o.getDescription())
        .ownerId(o.getOwnerId())
        .logoUrl(o.getLogoUrl())
        .email(o.getEmail())
        .phone(o.getPhone())
        .address(o.getAddress())
        .status(o.getStatus())
        .createdAt(o.getCreatedAt())
        .updatedAt(o.getUpdatedAt())
        .build();
  }

}
