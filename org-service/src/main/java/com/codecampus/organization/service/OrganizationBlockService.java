package com.codecampus.organization.service;

import com.codecampus.organization.dto.request.CreateBlockRequest;
import com.codecampus.organization.dto.response.BlockResponse;
import com.codecampus.organization.entity.OrganizationBlock;
import com.codecampus.organization.mapper.OrganizationBlockMapper;
import com.codecampus.organization.repository.OrganizationBlockRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrganizationBlockService {
  OrganizationBlockRepository repo;
  OrganizationBlockMapper mapper;

  @Transactional
  public BlockResponse createGrade(CreateBlockRequest req) {
    OrganizationBlock b = OrganizationBlock.builder()
        .orgId(req.getOrgId())
        .name(req.getName())
        .code(req.getCode())
        .description(req.getDescription())
        .build();
    b = repo.save(b);
    return mapper.toBlockResponseFromOrganizationBlock(b);
  }

  public List<BlockResponse> getAllGradesByOrganization(String orgId) {
    return repo.findByOrgId(orgId)
        .stream()
        .map(mapper::toBlockResponseFromOrganizationBlock)
        .collect(Collectors.toList());
  }
}