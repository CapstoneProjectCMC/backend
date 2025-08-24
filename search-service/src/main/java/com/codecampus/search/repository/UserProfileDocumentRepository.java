package com.codecampus.search.repository;

import com.codecampus.search.entity.UserProfileDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileDocumentRepository
        extends ElasticsearchRepository<UserProfileDocument, String> {
}