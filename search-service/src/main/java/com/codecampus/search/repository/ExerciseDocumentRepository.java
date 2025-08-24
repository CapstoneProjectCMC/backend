package com.codecampus.search.repository;

import com.codecampus.search.entity.ExerciseDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseDocumentRepository
    extends ElasticsearchRepository<ExerciseDocument, String> {
}
