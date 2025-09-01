package com.codecampus.search.repository;

import com.codecampus.search.entity.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostDocumentRepository
    extends ElasticsearchRepository<PostDocument, String> {
}