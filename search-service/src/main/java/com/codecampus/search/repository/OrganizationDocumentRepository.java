package com.codecampus.search.repository;

import com.codecampus.search.entity.OrganizationDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface OrganizationDocumentRepository
    extends ElasticsearchRepository<OrganizationDocument, String> {
}